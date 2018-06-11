package codesquad.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Size;

import codesquad.CannotDeleteException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    private static final Logger log =  LoggerFactory.getLogger(Question.class);

    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @Size(min = 3)
    @Lob
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    @JsonIgnore
    private List<Answer> answers = new ArrayList<>();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(String title, String contents, User writer) {
        this(0L,title, contents, writer);
    }

    public Question(long id, String title, String contents, User writer) {
        super(id);
        this.title = title;
        this.contents = contents;
        this.writer = writer;
    }

    public void update(User writer, Question target) {
        if (!isOwner(writer))
            throw new IllegalStateException("글쓴이만 수정할 수 있습니다.");
        this.title = target.title;
        this.contents = target.contents;
        log.info("update success");
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(this.title, this.contents, this.writer);
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public List<Answer> getAnswers() {
        return answers;
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
        log.info("isDeleted 호출");
        return deleted;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public List<DeleteHistory> delete(User loginUser) throws CannotDeleteException {
        if (!isOwner(loginUser) || answerWriterCheck())
            throw new CannotDeleteException("자신이 쓴 글만 삭제할 수 있습니다.");
        log.info("삭제성공");
        deleted = true;
        List<DeleteHistory> histories = deleteAnswer(loginUser);
//        List<DeleteHistory> histories = new ArrayList<>();
        histories.add(new DeleteHistory(ContentType.QUESTION ,getId(), loginUser, LocalDateTime.now()));
        return histories;
    }

    public List<DeleteHistory> deleteAnswer(User loginUser) throws CannotDeleteException {
        List<DeleteHistory> histories = new ArrayList<>();
        log.info("getAnswers : {}", getAnswers());
        for (Answer answer : getAnswers()) {
            log.info("for문호출");
            histories.add(answer.delete(loginUser));
        }
        return histories;
    }

    public boolean answerWriterCheck() {
        for (Answer answer : answers)
            if (!answer.isOwner(this.writer))
                return true;
        return false;
    }
}
