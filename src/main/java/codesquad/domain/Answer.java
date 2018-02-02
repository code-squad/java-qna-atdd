package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
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

    private boolean deleted = false;

    public Answer() {
    }

    public Answer(String contents) {
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

    public void writedBy(User writer) {
        this.writer = writer;
    }

    public AnswerDto toAnswerDto() {
        return new AnswerDto(getId(), this.contents);
    }

    public Answer update(User loginUser, AnswerDto answerDto) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("자신이 작성한 답변만 수정 가능합니다.");
        }
        this.contents = answerDto.getContents();
        return this;
    }

    public void delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("자신이 작성한 답변만 삭제 가능합니다.");
        }
        this.deleted = true;
    }

    @Override
    public String generateUrl() {
        return String.format("%s/answers/%d", question.generateUrl(), getId());
    }

    @Override
    public String generateRestUrl() {
        return String.format("%s/answers/%d", question.generateRestUrl(), getId());
    }

    @Override
    public String toString() {
        return "Answer [id=" + getId() + ", writer=" + writer + ", contents=" + contents + "]";
    }
}
