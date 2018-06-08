package codesquad.domain;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import java.time.LocalDateTime;

@Entity
public class Answer extends AbstractEntity implements UrlGeneratable {
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    private Question question;

    @Size(min = 5)
    @Lob
    private String contents;

    private boolean deleted = false;

    public Answer() {
    }

    public Answer(String contents) {
        this.contents = contents;
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

    public void writeBy(User user) {
        this.writer = user;
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

    public void toQuestion(Question question) {
        this.question = question;
    }

    public boolean isOwner(User user) {
        return writer.equals(user);
    }

    public boolean isDeleted() {
        return deleted;
    }

    DeleteHistory deleteAnswerByDeletedQuestion(User user) {
        deleted = true;
        return new DeleteHistory(ContentType.ANSWER, super.getId(), user, LocalDateTime.now());
    }

    public DeleteHistory deleteAnswerByOwner(User user) {
        if (!writer.equals(user)) {
            throw new UnAuthorizedException();
        }
        deleted = true;
        return new DeleteHistory(ContentType.ANSWER, super.getId(), user, LocalDateTime.now());
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }

    public AnswerDto toAnswerDto() {
        return new AnswerDto(contents);
    }
}
