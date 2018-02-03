package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.dto.QuestionDto;
import org.hibernate.annotations.Where;
import support.domain.UrlGeneratable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Question extends Contents implements UrlGeneratable {
    @Size(min = 3, max = 100)
    @Column(length = 100, nullable = false)
    private String title;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public Question() {
    }

    public Question(String title, String contents) {
        this(title, contents, null);
    }

    public Question(String title, String contents, User writer) {
        super(writer, contents);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void update(User loginUser, Question updatedQuestion) {
        super.update(loginUser, updatedQuestion.getContents());
        this.title = updatedQuestion.title;
    }

    public List<DeleteHistory> deleteWithAnswers(User loginUser) throws CannotDeleteException{
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        deleteHistories.add(delete(loginUser));
        deleteHistories.addAll(deleteAnswers(loginUser));
        return deleteHistories;
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    private List<DeleteHistory> deleteAnswers(User loginUser) throws CannotDeleteException {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : answers) {
            if(!answer.isDeleted())
                deleteHistories.add(answer.delete(loginUser));
        }
        return deleteHistories;
    }

    public int getAnswerCount() {
        return (int)answers.stream().filter(answer -> !answer.isDeleted()).count();
    }

    public List<Answer> getAnswers() {
        return answers.stream().filter(answer -> !answer.isDeleted()).collect(Collectors.toList());
    }

    @Override
    public String generateUrl() {
        return String.format("/questions/%d", getId());
    }

    public QuestionDto toQuestionDto() {
        return new QuestionDto(getId(), this.title, getContents());
    }

    @Override
    public String toString() {
        return "Question [id=" + getId() + ", title=" + title + ", contents=" + getContents() + ", writer=" + getWriter() + "]";
    }

    @Override
    public DeleteHistory audit() {
        return new DeleteHistory(ContentType.QUESTION, getId(), getWriter());
    }
}
