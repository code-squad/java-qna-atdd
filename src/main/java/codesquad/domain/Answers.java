package codesquad.domain;

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
    private List<Answer> answers = new ArrayList<>();

    public void remove(Answer answer) {
        answers.remove(answer);
    }

    public List<Answer> getAll() {
        return answers;
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    public List<DeleteHistory> delete(User user) throws CannotDeleteException {
        List<DeleteHistory> histories = new ArrayList<>();
        for (Answer answer : answers)  {
            histories.add(delete(answer, user));
        }
        return histories;
    }

    private DeleteHistory delete(Answer answer, User user) throws CannotDeleteException {
        try {
            return answer.delete(user);
        } catch (CannotDeleteException e) {
            throw new CannotDeleteException("타인의 답변이 포함 된 질문입니다.");
        }
    }
}
