package codesquad.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;

import codesquad.CannotDeleteException;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

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
    private Answers answers;

    private boolean deleted = false;

    public Question() {
        answers = new Answers();
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
        this.answers = new Answers();
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

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public Question update(User loginUser, Question updatedQuestion) throws IllegalAccessException {
        if (!loginUser.isWriterOf(this)) {
            throw new IllegalAccessException("작성자만 수정할 수 있습니다.");
        }
        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
        return this;
    }

    public List<DeleteHistory> delete(User loginUser, Question question) throws CannotDeleteException {
        List<DeleteHistory> histories = new ArrayList();
        if (!validateDeleteQuestion(loginUser, question)) {
            throw new CannotDeleteException("지울 수 없는 질문입니다.");
        }
        histories.add(deleteAndMakeHistory(loginUser));
        histories.addAll(deleteAllAnswer(loginUser));
        return histories;
    }

    private DeleteHistory deleteAndMakeHistory(User loginUser) {
        deleted = true;
        return new DeleteHistory(ContentType.QUESTION, getId(), loginUser, getCreateDate());
    }

    private List<DeleteHistory> deleteAllAnswer(User loginUser) throws CannotDeleteException {
        List<DeleteHistory> histories = new ArrayList();
        histories = answers.delete(histories, loginUser);
        return histories;
    }

    private boolean validateDeleteQuestion(User loginUser, Question question) {
        boolean valid = false;
        if (!loginUser.isWriterOf(question)) {
            return false;
        }
        valid = answers.validate(valid, loginUser);
        return valid;
    }
}
