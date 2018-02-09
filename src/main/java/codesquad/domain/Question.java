package codesquad.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Where;

import codesquad.dto.QuestionDto;
import org.hibernate.sql.Delete;
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

    @Embedded
    private Answers answers = new Answers();

    private boolean deleted = false;

    public Question() {
    }

    public Question(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question(QuestionDto questionDto) {
        this.title = questionDto.getTitle();
        this.contents = questionDto.getContents();
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

    public void updateBy(Question updatedQuestion, User loginUser) {
        if ( !this.isOwner(loginUser)) {
            throw new IllegalStateException("loginUser is not owner, loginUser=" + loginUser + ", question=" + this);
        }

        this.title = updatedQuestion.title;
        this.contents = updatedQuestion.contents;
    }

    public List<DeleteHistory> deleteBy(User loginUser) {
        if ( !this.isOwner(loginUser)) {
            throw new IllegalStateException("loginUser is not owner of question, loginUser=" + loginUser + ", question=" + this);
        }

        List<DeleteHistory> histories = answers.deleteBy(loginUser, getId());

        this.deleted = true;
        histories.add(new DeleteHistory(ContentType.QUESTION, getId(), loginUser));

        return histories;
    }

    public Answer findAnswer(long answerId) {
        return answers.findAnswer(answerId);
    }

    public int getAnswersCount() {
        return answers.count();
    }
}
