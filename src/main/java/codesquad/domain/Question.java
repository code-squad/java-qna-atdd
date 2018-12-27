package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.security.LoginUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static codesquad.domain.User.GUEST_USER;
import static org.slf4j.LoggerFactory.getLogger;

@Entity
public class Question extends AbstractEntity implements UrlGeneratable {
    private static final Logger log = getLogger(Question.class);

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
    // fetch = FetchType.LAZY , fetch = FetchType.EGGER의 차이점.
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    @JsonIgnore
    private List<Answer> answers = new ArrayList<>();           //1개 테이블과 n개의 객체와 맵핑하는게 올바른 설계 , 1개 테이블과 1개 객체 맵핑(올바른 설계가 아닐수 있다.)

    private boolean deleted = false;

    private LocalDateTime createDate = LocalDateTime.now();

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
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

    public void writtenBy(User loginUser) {
        if (isLogin(loginUser)) {
            this.writer = loginUser;
        }
    }

    public Answer addAnswer(User loginUser, Answer answer) {
        if (isLogin(loginUser)) {
            answer.toQuestion(this);
            answers.add(answer);
        }
        return answer;
    }

    public boolean isLogin(User loginUser) {
        if (Objects.isNull(loginUser)) {
            throw new UnAuthenticationException();
        }
        return true;
    }

    public boolean isOwner(User loginUser) {
        if (isLogin(loginUser)){
            return writer.equals(loginUser);
        }
        throw new UnAuthorizedException();
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Question delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }

        long otherUserCount = answers.stream()
                .filter(answer -> !answer.isOwner(writer))
                .filter(answer -> !answer.isDeleted())
                .count();

        if (otherUserCount > 0) {
            throw new CannotDeleteException();
        }

        for (Answer answer : answers) {
            answer.delete(writer);
        }

        deleted = true;
        return this;
    }

    public List<DeleteHistory> createDeleteHistories(long id) {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, id, writer));
        for (Answer answer : answers) {
            deleteHistories.add(answer.createAnswerOfDeleteHistory());
        }
        return deleteHistories;
    }

    public Question modify(Question updateQuestion, User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();      //forbidden
        }
        contents = updateQuestion.contents;
        title = updateQuestion.title;
        return this;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }


}
