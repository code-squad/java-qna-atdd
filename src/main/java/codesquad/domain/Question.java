package codesquad.domain;

import java.util.ArrayList;
import java.util.List;

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

    public void deleteBy(User loginUser) {
        if ( !this.isOwner(loginUser)) {
            throw new IllegalStateException("loginUser is not owner, loginUser=" + loginUser + ", question=" + this);
        }

        this.deleted = true;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public Answer findAnswer(long answerId) {
        List<Answer> answers = getAnswers();
        for (Answer answer : answers) {
            if (answer.getId() == answerId) {
                return answer;
            }
        }

        return null;
    }

    public int getAnswersCount() {
        return answers.size();
    }
}
