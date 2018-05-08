package codesquad.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.*;
import javax.validation.constraints.Size;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import support.domain.AbstractEntity;
import support.domain.UrlGeneratable;

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
        this(0L, title, contents);
    }

    public Question(long id, String title, String contents) {
        super(id);
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
        this.writer = loginUser;
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
        return new QuestionDto(getId(), this.title, this.contents, this.writer, this.answers);
    }

    public Question update(User loginUser, Question updatedQuestion) {
        if (!this.isOwner(loginUser))
            throw new UnAuthorizedException();

        this.title = updatedQuestion.getTitle();
        this.contents = updatedQuestion.getContents();
        return this;
    }

    public void delete(User loginUser) throws CannotDeleteException {
        if (!this.isOwner(loginUser))
            throw new UnAuthorizedException();

        final boolean match = this.answers.stream()
                .anyMatch(answer -> !answer.getWriter().equals(loginUser));
        if (match)
            throw new CannotDeleteException("다른 사용자의 답변이 존재합니다.");

        this.deleted = true;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public Answer getAnswer(long answerId) {
        return this.answers.stream().filter(answer -> answer.getId() == answerId).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + contents + ", writer=" + writer + "]";
    }
}
