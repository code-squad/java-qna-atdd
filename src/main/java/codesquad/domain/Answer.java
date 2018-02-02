package codesquad.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
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

    public Answer(String contents) {
        this.contents = contents;
        this.deleted = false;
    }

    public Answer(long id, String contents) {
        super(id);
        this.contents = contents;
        this.deleted = false;
    }

    public Answer(Long id, User writer, Question question, String contents) {
        super(id);
        this.writer = writer;
        this.question = question;
        this.contents = contents;
        this.deleted = false;
    }

    public void delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.deleted = true;
        removeFromQuestion();
    }

    private void removeFromQuestion() {
        this.question.removeAnswer(this);
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
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

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public AnswerDto toAnswerDto() {
        return new AnswerDto(this.getId(), this.contents);
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
