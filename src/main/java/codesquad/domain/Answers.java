package codesquad.domain;

import codesquad.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    List<Answer> answers = new ArrayList();

    public void add(Answer answer) {
        answers.add(answer);
    }

    public List<DeleteHistory> delete(List<DeleteHistory> histories, User loginUser) throws CannotDeleteException {
        if (answers.isEmpty()) {
            return histories;
        }
        for (Answer answer : answers) {
            histories.add(answer.delete(loginUser));
        }
        return histories;
    }

    public boolean validate(boolean valid, User loginUser) {
        if (answers.isEmpty()) {
            valid = true;
        }
        if (!answers.isEmpty()) {
            valid = hasOnlyOwnAnswer(loginUser);
        }
        return valid;
    }

    private boolean hasOnlyOwnAnswer(User loginUser) {
        Long cnt = answers.stream()
                          .filter(answer -> !answer.isOwner(loginUser))
                          .count();
        return cnt == 0;
    }
}
