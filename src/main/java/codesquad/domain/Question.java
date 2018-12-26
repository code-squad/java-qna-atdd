package codesquad.domain;

import codesquad.exception.CannotDeleteException;
import codesquad.exception.UnAuthorizedException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @JsonIgnore
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

    public Question(long id, User writer, String title, String contents) {
        super(id);
        this.writeBy(writer);
        this.title = title;
        this.contents = contents;
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if (!matchUserId(loginUser.getUserId())) {
            throw new CannotDeleteException("작성자만 삭제 가능합니다.");
        }

        if(!isDeletable()) {
            throw new CannotDeleteException("삭제할 수 없는 답변이 포함돼있습니다.");
        }

        List<DeleteHistory> temp = new ArrayList<>();

        this.deleted = true;
        temp.add(new DeleteHistory(ContentType.QUESTION, getId(), writer));
        temp.addAll(deleteAllAnswers(loginUser));

        return temp;
    }

    private List<DeleteHistory> deleteAllAnswers(User loginUser) throws CannotDeleteException {
        List<DeleteHistory> temp = new ArrayList<>();
        if(!answers.isEmpty()) {
            for (Answer answer : answers) {
                temp.addAll(answer.delete(loginUser));
            }
        }
        return temp;
    }

    public void update(User loginUser, Question updatedQuestion) {
        if (!matchUserId(loginUser.getUserId())) {
            throw new UnAuthorizedException();
        }

        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
    }

    public boolean matchUserId(String userId) {
        return this.writer.matchUserId(userId);
    }

    public boolean isDeletable() {
        if(answers.isEmpty()) {
            return true;
        }

        return isAllOwnWritten();
    }

    private boolean isAllOwnWritten() {
        for (Answer answer : answers) {
            if(!answer.isOwner(writer)){
                return false;
            }
        }
        return true;
    }

    public Answer addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
        return answer;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public boolean equalsTitleAndContents(Question target) {
        if (Objects.isNull(target)) {
            return false;
        }

        return title.equals(target.title) &&
                contents.equals(target.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, contents, writer);
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
