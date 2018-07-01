package codesquad.domain;

import java.time.LocalDateTime;
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

import codesquad.UnAuthorizedException;
import org.apache.tomcat.jni.Local;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import org.hibernate.sql.Delete;
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

    // 테스트 할 때 댓글 삭제 물리적인 삭제 가 아니라 논리적인 삭제, ㅇㅁ너라ㅣㅇ너라ㅣㅇㄴ멂아ㅣ;ㄴ러마ㅣ;ㅇㄴ럼;이ㅏㄴ럼;ㅣㄴ러이걸 어떻게 해결해야 하나요??
    @OneToMany(mappedBy = "question")
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    // 질문이 생성되어 있을때 기본 deleted 상태 false, 지울때 true
    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void update(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public List<DeleteHistory> delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("삭제 할 수 없습니다.");
        }
        List<DeleteHistory> deleteHistories = deleteAnswer(loginUser);
        deleted = true;
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser, LocalDateTime.now()));
        return deleteHistories;
    }

    public List<DeleteHistory> deleteAnswer(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("다른 사용자의 답변이 존재합니다.");
        }
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            deleteHistories.add(answers.get(i).delete(loginUser));
        }
        return deleteHistories;
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
}
