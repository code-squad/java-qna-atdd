package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
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

    public Question(@Size(min = 3, max = 100) String title, @Size(min = 3) String contents, User writer, List<Answer> answers, boolean deleted) {
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.answers = answers;
        this.deleted = deleted;
    }

    public Question(long id, @Size(min = 3, max = 100) String title, @Size(min = 3) String contents, User writer, List<Answer> answers, boolean deleted) {
        super(id);
        this.title = title;
        this.contents = contents;
        this.writer = writer;
        this.answers = answers;
        this.deleted = deleted;
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

    public List<DeleteHistory> delete(User loginUser) {
        if (!loginUser.equals(this.writer)) {
            throw new CannotDeleteException("you can't delete the other's information.");
        }
        List<DeleteHistory> histories = Answer.deleteAnswers(this.answers, loginUser);
        this.deleted = true;
        histories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser));
        return histories;
    }

    public void update(Question updateQuestion, User loginUser) throws UnAuthorizedException {
        if (!this.writer.equals(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.contents = updateQuestion.contents;
        this.title = updateQuestion.title;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Question question = (Question) o;
        return deleted == question.deleted &&
                Objects.equals(title, question.title) &&
                Objects.equals(contents, question.contents) &&
                Objects.equals(writer, question.writer) &&
                Objects.equals(answers, question.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, contents, writer, answers, deleted);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public boolean equalsQuestion(Question question) {
        return this.title.equals(question.title) && this.contents.equals(question.contents);
    }

}
