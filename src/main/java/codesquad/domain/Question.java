package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.ApiUrlGeneratable;
import support.domain.UrlGeneratable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable, ApiUrlGeneratable {
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

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public User getWriter() {
        return writer;
    }

    public int getCountOfAnswers() {
        return answers.getCountOfAnswers();
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.addAnswer(answer);
    }

    public boolean isOwner(User loginUser) {
        if (this.writer == null) {
            throw new IllegalStateException();
        }
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public List<DeleteHistory> delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("권한이없습니다");
        }
        List<DeleteHistory> histories = answers.deleteAll(loginUser);
        this.deleted = true;
        histories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser, LocalDateTime.now()));

        return histories;
    }

    public Question update(User loginUser, Question updatedQuestion) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
        return this;
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, this.contents);
    }

    public void checkAuthority(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public URI generateApiUri() {
        String apiUri = "/api" + generateUrl();
        return URI.create(apiUri);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
