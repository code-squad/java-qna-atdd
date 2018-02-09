package codesquad.domain;

import codesquad.UnAuthorizedException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    @JsonIgnore
    private Question question;

    @Size(min = 5)
    @Lob
    private String contents;

    private boolean deleted = false;

    public Answer() {
    }

    public Answer(User writer, String contents) {
        this.writer = writer;
        this.contents = contents;
    }

    public Answer(Long id, User writer, Question question, String contents) {
        super(id);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
        this.deleted = false;
    }

    public User getWriter() {
        return writer;
    }

    public Question getQuestion() {
        return question;
    }

    public String getContents() {
        return contents;
    }

    public void upadte(User user, String contents) {
        if (!isOwner(user)) {
            throw new UnAuthorizedException();
        }
        this.contents = contents;
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public DeleteHistory delete(User loginUser) {
        if (!this.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        this.deleted = true;

        return makeDeleteHistory(loginUser);
    }

    private DeleteHistory makeDeleteHistory(User loginUser) {
        return new DeleteHistory(ContentType.ANSWER, this.getId(), loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }
}
