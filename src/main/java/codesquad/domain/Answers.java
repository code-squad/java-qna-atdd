package codesquad.domain;

import codesquad.etc.CannotDeleteException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Answers {

    @JsonIgnore
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    List<Answer> answers;

    public List<Answer> getAnswers() {
        return answers;
    }

    public Answers setAnswers(List<Answer> answers) {
        this.answers = answers;
        return this;
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answers answers1 = (Answers) o;
        return Objects.equals(answers, answers1.answers);
    }

    @Override
    public int hashCode() {

        return Objects.hash(answers);
    }

    @Override
    public String toString() {
        return "Answers{" +
                "answers=" + answers +
                '}';
    }

    public List<DeleteHistory> delete (User loginUser) throws CannotDeleteException {
        List<DeleteHistory> deleteHistoryList = new ArrayList<>();
        for(Answer answer : answers) {
            deleteHistoryList.add(answer.delete(loginUser));
        }

        return deleteHistoryList;
    }
}
