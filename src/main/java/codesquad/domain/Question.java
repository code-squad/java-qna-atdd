package codesquad.domain;

import codesquad.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

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

    @Embedded
    private Answers answers = new Answers();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(User loginUser, QuestionDto questionDto) {
        this.title = questionDto.getTitle();
        this.contents = questionDto.getContents();
        writeBy(loginUser);
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public User getWriter() {
        return writer;
    }

    public List<Answer> getAnswers() {
        return answers.getAll();
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public void removeAnswer(Answer answer) {
        answers.remove(answer);
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Question update(User user, QuestionDto questionDto) throws CannotUpdateException {
        if (!isOwner(user)) {
            throw new CannotUpdateException("본인의 질문만 수정 할 수 있습니다.");
        }

        title = questionDto.getTitle();
        contents = questionDto.getContents();
        return this;
    }

    public List<DeleteHistory> delete(User user) throws CannotDeleteException {
        if (!isOwner(user)) {
            throw new CannotDeleteException("본인의 질문만 삭제 할 수 있습니다.");
        }

        List<DeleteHistory> histories = new ArrayList<>();
        histories.addAll(answers.delete(user));

        deleted = true;
        histories.add(new DeleteHistory(this, user));

        return histories;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String resourceUrl() {
        return String.format("/api/questions/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
