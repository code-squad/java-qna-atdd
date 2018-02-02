package codesquad.domain;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import codesquad.dto.AnswerDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

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

    public Answer(AnswerDto answerDto) {
        contents = answerDto.getContents();
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

    private boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
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

    public AnswerDto toAnswerDto() {
        return new AnswerDto(getId(), this.contents);
    }

    public void updatedBy(User loginUser, long questionId, String contents) {
        validate(loginUser, questionId);

        this.contents = contents;
    }

    private void validate(User loginUser, long questionId) {
        if ( !this.isOwner(loginUser)) {
            throw new IllegalStateException("loginUser is not owner, loginUser=" + loginUser + ", answer=" + this);
        }

        if (question.getId() != questionId) {
            throw new IllegalStateException("questionId is invalid, loginUser=" + loginUser + ", questionId=" + questionId);
        }
    }

    public void deleteBy(User loginUser, long questionId) {
        validate(loginUser, questionId);

        this.deleted = true;
    }
}
