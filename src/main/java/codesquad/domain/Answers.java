package codesquad.domain;

import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Answers {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public void add(Answer answer) {
        answers.add(answer);
    }

    public Answer findAnswer(long answerId) {
        for (Answer answer : answers) {
            if (answer.getId() == answerId) {
                return answer;
            }
        }

        return null;
    }

    public int count() {
        return answers.size();
    }

    public List<DeleteHistory> deleteBy(User loginUser, long questionId) {
        if (answers.stream().anyMatch(answer -> !answer.isOwner(loginUser))) {
            throw new IllegalStateException("loginUser is not owner of answers, loginUser=" + loginUser + ", questionId=" + questionId);
        }

        return answers.stream().map(answer -> answer.deleteBy(loginUser, questionId)).collect(Collectors.toList());
    }
}
