package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

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

    public void delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("자신이 작성한 질문만 삭제 가능합니다.");
        }
        this.deleted = true;
    }

    public Question update(User loginUser, QuestionDto questionDto) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("자신이 작성한 질문만 수정 가능합니다.");
        }
        this.title = questionDto.getTitle();
        this.contents = questionDto.getContents();
        return this;
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

    @Override
    public String generateRestUrl() {
        return String.format("/api/questions/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
