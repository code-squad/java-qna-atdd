package codesquad.domain;

import codesquad.security.HttpSessionUtils;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Question updateQuestion(Question updatedQuestion) {
        this.contents = updatedQuestion.contents;
        this.title = updatedQuestion.title;
        return this;
    }

    public Question applyOwner(User user) {
        if(user != null && user.equals(writer)) {
            this.owner = true;
        }
        return this;
    }

    /* 피드백1) 본인이 작성한 글인지 확인하는 로직이 서비스 영역 vs 도메인 영역
        나의 선택) 도메인 영역
            why? Service 영역의 역할은 데이터를 가공하는 역할이기 때문에 확인하는 메소드는 도메인이 맞지 않을까?
                 그러나, Service 영역에서 한다면, 도메인까지 넘어가지 않고 바로 처리를 할 수 있다는 장점은 있을 것 같다.
                 과연 답은..?
    */
    public boolean isOneSelf(User loginUser) {
        return writer.equals(loginUser);
    }

    public boolean isTitleAndContentsAndWriter(Question question) {
        return this.title.equals(question.title) && this.contents.equals(question.contents)
                && this.writer.equals(question.writer);
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

    public void deleteAnswer(Answer answer) {
        answer.toQuestion(this);
        System.out.println("BEFORE" + answers.size());
        this.answers.remove(answer);
        System.out.println("AFTER" + answers.size());
    }
}
