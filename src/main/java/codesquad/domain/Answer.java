package codesquad.domain;

import codesquad.CannotManageException;
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
    private Question question;

    @Size(min = 5)
    @Lob
    private String contents;

    @Column
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

    public static Answer convert(User loginUser, String contents) {
        return new Answer(loginUser, contents);
    }

    public Answer update(User loginUser, Answer updatedAnswer) throws CannotManageException {
        if(!this.isOwner(loginUser)) { throw new CannotManageException("수정은 글쓴이만 가능합니다."); }
        else if(isDeleted()) { throw new CannotManageException("삭제된 글입니다."); }

        this.contents = updatedAnswer.getContents();
        return this;
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }

    public void deleted(User loginUser) throws CannotManageException {
        if(!this.isOwner(loginUser)) { throw new CannotManageException("삭제는 글쓴이만 가능합니다."); }
        this.deleted = true;
    }
}
