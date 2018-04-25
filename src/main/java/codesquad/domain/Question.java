package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.dto.QuestionDto;
import org.hibernate.annotations.Where;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

import javax.persistence.*;
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

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(QuestionDto question) {
        this.title = question.getTitle();
        this.contents = question.getContents();
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
        return answers;
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

    public void update(User loginUser, Question updatedQuestion) throws UnAuthenticationException {
        if( ! isOwner(loginUser)) {
            throw new UnAuthenticationException();
        }

        this.title = updatedQuestion.getTitle();
        this.contents = updatedQuestion.getContents();
    }

    public void delete(User loginUser) throws CannotDeleteException {
        if( ! isOwner(loginUser)) {
            throw new CannotDeleteException("내가 작성한 글만 삭제 가능합니다.");
        }
        this.deleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        if (!super.equals(o)) return false;
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
