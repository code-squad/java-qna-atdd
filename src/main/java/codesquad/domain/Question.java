package codesquad.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;

import codesquad.CannotManageException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    @Column
    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    private Question updateTitle(String title) {
        if(StringUtils.isEmpty(title)) { throw new IllegalArgumentException(); }
        this.title = title;
        return this;
    }

    private Question updateContents(String contents) {
        if(StringUtils.isEmpty(contents)) { throw new IllegalArgumentException(); }
        this.contents = contents;
        return this;
    }

    public Question update(User loginUser, Question updatedQuestion) throws CannotManageException {
        if(!this.isOwner(loginUser)) { throw new CannotManageException("수정은 글쓴이만 가능합니다."); }
        else if(isDeleted()) { throw new CannotManageException("삭제된 글입니다."); }

        updateTitle(updatedQuestion.getTitle());
        updateContents(updatedQuestion.getContents());
        return this;
    }

    public void deleted(User loginUser) throws CannotManageException {
        if(!this.isOwner(loginUser)) { throw new CannotManageException("수정은 글쓴이만 가능합니다."); }
        this.deleted = true; //TODO:4단계에서 삭제히스토리 테이블에 저장
    }

    public static Question convert(QuestionDto questionDto) {
        return new Question(questionDto.getTitle(), questionDto.getContents());
    }
}
