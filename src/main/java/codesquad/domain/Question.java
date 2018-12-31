package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
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

    public void update(User loginUser, Question updatedQuestion) {
        if (!isOwner(loginUser)) throw new UnAuthorizedException();
        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException{
        if (!isOwner(loginUser)) throw new CannotDeleteException("You can't delete this article.");
        if(!isDeletable()) throw new CannotDeleteException("You can't delete this article.");

        this.deleted = true;
        List<DeleteHistory> temp = new ArrayList<>();
        temp.add(new DeleteHistory(getId(), ContentType.QUESTION, writer));
        temp.addAll(deleteAllAnswers(loginUser));

        return temp;
    }

    public boolean isDeletable() {
        if(answers.isEmpty()) return true;

        return isAllOwnWritten();
    }

    private boolean isAllOwnWritten() {
        for (Answer answer : answers) {
            if(!answer.isOwner(writer)) return false;
        }
        return true;
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

    public boolean isEqual(Question question) {
        if (Objects.isNull(question)) {
            return false;
        }

        return title.equals(question.title) &&
                contents.equals(question.contents);
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
