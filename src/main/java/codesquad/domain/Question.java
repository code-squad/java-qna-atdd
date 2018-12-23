package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.security.HttpSessionUtils;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {

    private static final Logger logger = getLogger(Question.class);

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

    private boolean deleted = false;

    @Transient
    private boolean owner = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(String title, String contents, User writer) {
        this(title, contents);
        this.writer = writer;
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContents() {
        return contents;
    }

    public Question setContents(String contents) {
        this.contents = contents;
        return this;
    }

    public User getWriter() {
        return writer;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Question updateQuestion(User loginUser, Question updatedQuestion) throws UnAuthorizedException {
        if(!updatedQuestion.writer.equals(loginUser)) {
            throw new UnAuthorizedException();
        }
        this.contents = updatedQuestion.contents;
        this.title = updatedQuestion.title;
        return this;
    }

    public Question deleteQuestion(User loginUser) throws UnAuthorizedException{
        // 질문자와 로그인 유저가 동일 확인
        if(!this.writer.equals(loginUser)) {
            throw new UnAuthorizedException();
        }

        answers.stream().filter(a -> !a.isDeleted()).filter(a -> a.isOwner(this.writer))
                .forEach(a -> logger.debug("DeleteQuestion : Answer : {}", a.toString()));

        // 질문자와 답변자가 동일 확인
        if(answers.stream().filter(a -> !a.isOwner(this.writer)).count() > 0) {
            throw new UnAuthorizedException();
        }

        this.deleted = true;
        /* 피드백2) 질문삭제 시, 답변 삭제 처리(deleted) */
        deleteAnswers(loginUser);
        return this;
    }

    public void deleteAnswers(User loginUser) {
        for(Answer answer : answers) {
            answer.deleteAnswer(loginUser);
        }
    }

    public Question applyOwner(User user) {
        if(user != null && user.equals(writer)) {
            this.owner = true;
        }
        return this;
    }

    public boolean isOneSelf(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isTitleAndContentsAndWriter(Question question) {
        return this.title.equals(question.title) && this.contents.equals(question.contents)
                && this.writer.equals(question.writer);
    }

    public DeleteHistory createQuestionOfDeleteHistory(Long id) {
        return new DeleteHistory(ContentType.QUESTION, id, writer);
    }

    public List<DeleteHistory> createAnswersOfDeleteHistories() {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : answers) {
            deleteHistories.add(answer.createAnswerOfDeleteHistory());
        }
        return deleteHistories;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(title, question.title) &&
                Objects.equals(contents, question.contents) &&
                Objects.equals(writer, question.writer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, contents, writer);
    }
}
