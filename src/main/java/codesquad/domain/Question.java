package codesquad.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.Size;

import codesquad.exceptions.UnAuthorizedException;
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

    public Question update(User loginUser, QuestionDto updatedQuestion) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("owner is not match. permission denied!");
        }

        this.title = updatedQuestion.toQuestion().title;
        this.contents = updatedQuestion.toQuestion().contents;
        return this;
    }

    public boolean isDeletable(User loginUser) {
        return isOwner(loginUser) && answers.stream().allMatch(a -> a.isOwner(loginUser));
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

    public void logicalDelete() {
        this.deleted = true;
    }

    public List<DeleteHistory> toDeleteHistories(User loginUser) {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, this.getId(), loginUser));
        deleteHistories.addAll(toAnswerHistories(loginUser));
        return Collections.unmodifiableList(deleteHistories);
    }

    private List<DeleteHistory> toAnswerHistories(User loginUser) {
        return answers.stream().map(a -> {
            a.logicalDelete();
            return new DeleteHistory(ContentType.ANSWER, a.getId(), loginUser);
        }).collect(Collectors.toList());
    }
}
