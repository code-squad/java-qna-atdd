package codesquad.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.Size;

import codesquad.etc.CannotDeleteException;
import codesquad.etc.UnAuthorizedException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {

    private static final Logger log = LoggerFactory.getLogger(Question.class);

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
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public Question setWriter(User writer) {
        this.writer = writer;
        return this;
    }

    public void addAnswer(Answer answer) {
        answers.addAnswer(answer);
    }

    public Answers getAnswers() {
        return answers;
    }

    public Question setAnswers(Answers answers) {
        this.answers = answers;
        return this;
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

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if (!writer.equals(loginUser))
            throw new CannotDeleteException("Question delete failed. ");

        this.deleted = true;
        List<DeleteHistory> historyList = new ArrayList<>();
        historyList.add(new DeleteHistory(ContentType.QUESTION,
                getId(),
                loginUser,
                LocalDateTime.now()));

        for(Answer answer : answers.getAnswers()) {
            answer.delete(loginUser);

            historyList.add(new DeleteHistory(ContentType.ANSWER,
                    answer.getId(),
                    loginUser,
                    LocalDateTime.now()));
        }

        return historyList;
    }

    public void update(User loginUser, Question newQuestion) {
        if (this.writer.equals(loginUser)) {
            this.contents = newQuestion.getContents();
            this.title = newQuestion.getTitle();

            return;
        }
        log.debug("{} {}", loginUser, writer);
        throw new UnAuthorizedException("Cannot update question.");
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public String generateUrl() {
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
