package codesquad.domain;

import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

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

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Answer update(User user, String content) throws CannotUpdateException {
        if (!isOwner(user)) {
            throw new CannotUpdateException("본인의 답변만 수정 할 수 있습니다.");
        }

        this.contents = content;
        return this;
    }

    public DeleteHistory delete(User user) throws CannotDeleteException {
        if (!isOwner(user)) {
            throw new CannotDeleteException("본인의 답변 삭제 할 수 있습니다.");
        }
        deleted = true;

        return new DeleteHistory(this, user);
    }

    @Override
    public String generateUrl() {
        return answerUrlOf(question.generateUrl());
    }

    @Override
    public String resourceUrl() {
        return answerUrlOf(question.resourceUrl());
    }

    private String answerUrlOf(String questionUrl) {
        return String.format("%s/answers/%d", questionUrl, getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }
}
