package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty
    private String title;

    @Size(min = 3)
    @Lob
    @JsonProperty
    private String contents;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_question_writer"))
    @JsonProperty
    private User writer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
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
        writer = loginUser;
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
        return String.format("/qna/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), title, contents);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }

    public Question update(Question target, User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("question writer not equal");
        }

        title = target.title;
        contents = target.contents;

        return this;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        if (!super.equals(o)) return false;
//        Question question = (Question) o;
//        return deleted == question.deleted &&
//                Objects.equals(title, question.title) &&
//                Objects.equals(contents, question.contents) &&
//                Objects.equals(writer, question.writer) &&
//                Objects.equals(answers, question.answers);
//    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, contents, writer, answers, deleted);
    }
}
