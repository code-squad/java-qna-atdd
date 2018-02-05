package codesquad.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.validation.constraints.Size;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.security.LoginUser;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
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

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    @CreationTimestamp
    private Date createdTime;

    private boolean deleted;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
        this.deleted = false;
    }

    public void update(User loginUser, Question target) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        this.title = target.getTitle();
        this.contents = target.getContents();
    }

    public List<DeleteHistory> delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        if (!areAnswersAllMineOrEmpty()) {
            throw new UnAuthorizedException("모든 답변이 자신의 것이어야 글을 삭제할 수 있습니다.");
        }
        List<DeleteHistory> histories = new ArrayList<>();
        histories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser, LocalDateTime.now()));
        this.deleted = true;
        histories.addAll(deleteAnswers(loginUser));
        return histories;
    }

    private List<DeleteHistory> deleteAnswers(User loginUser) {
        List<DeleteHistory> histories = new ArrayList<>();
        answers.forEach(answer -> histories.add(answer.delete(loginUser)));
        return histories;
    }

    public boolean areAnswersAllMineOrEmpty() {
        if (answers.size() == 0) return true;
        log.debug("it's answers!! : {}", answers.toArray()[0].getClass());
        List <Answer> tempAnswers = answers;

        boolean result = tempAnswers.stream()
                .allMatch(answer -> answer.getWriter().equals(writer) || answer.isDeleted());
        log.debug("Result of areAnswersAllMineOrEmpty : {}", result);
        return result;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public void setWriter(User writer) {
        this.writer = writer;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    @Transactional
    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    @Transactional
    public void removeAnswer(Answer answer) {
        answers.remove(answer);
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
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + ", deleted=" + deleted + "]";
    }

}
