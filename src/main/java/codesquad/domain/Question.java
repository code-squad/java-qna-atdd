package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.security.LoginUser;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

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

    public Question setTitleAndContents(String title, String contents) {
        return setTitle(title).setContents(contents);
    }

    public User getWriter() {
        return writer;
    }

    public void writeBy(User loginUser) {
        this.writer = loginUser;
    }

    public Question addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
        return this;
    }

    public boolean isOwner(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void delete(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        deleted = true;
    }

    public Question modify(Question updateQuestion, User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException();      //forbidden
        }
        contents = updateQuestion.contents;
        title = updateQuestion.title;
        return this;
    }

    public boolean equalsWriter(User loginUser) {
        if (Objects.isNull(loginUser)){
            return false;
        }
        return isOwner(loginUser);
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
