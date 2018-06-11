package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;

import static codesquad.domain.ContentType.ANSWER;

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

    public Answer writeBy(User writer) {
        if (this.writer == null) {
            this.writer = writer;
        }
        return this;
    }

    public Answer toQuestion(Question question) {
        if (this.question == null) {
            this.question = question;
        }
        return this;
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

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    public String questionPath() {
        return question.generateApiUrl();
    }

    public DeleteHistory delete(User loginUser) throws CannotDeleteException {
        if (!writer.equals(loginUser)) {
            throw new CannotDeleteException("not match answer writer");
        }
        deleted = true;
        return DeleteHistory.convert(ANSWER, loginUser, this);
    }

    public AnswerDto update(User loginUser, AnswerDto updatedAnswer) {
        checkAuthority(loginUser);
        this.contents = updatedAnswer.getContents();
        return toAnswerDto();
    }

    private void checkAuthority(User loginUser) {
        if (!writer.equals(loginUser)) {
            throw new UnAuthorizedException();
        }
    }

    public AnswerDto toAnswerDto() {
        return new AnswerDto(getId(), contents);
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }
}
