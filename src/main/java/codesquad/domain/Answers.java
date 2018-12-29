package codesquad.domain;

import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Answers {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public void add(Answer answer) {
        answers.add(answer);
    }

    public long otherAnswerCount(User writer) {
        return answers.stream()
                .filter(answer -> !answer.isOwner(writer))
                .filter(answer -> !answer.isDeleted())
                .count();
    }

    public List<DeleteHistory> delete(User loginUser) {
        List<DeleteHistory> histories = new ArrayList<>();
        for (Answer answer : answers) {
            histories.add(answer.delete(loginUser));
        }
        return histories;
    }
}
