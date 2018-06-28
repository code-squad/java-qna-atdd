package codesquad.domain;

import codesquad.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class Answers {
    private static final int FIRST = 0;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public Answers() {

    }

    public Answers(List<Answer> answers) {
        this.answers = answers;
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    public int getSize() {
        return answers.size();
    }

    @Override
    public String toString() {
        return "Answers{" +
                "answers=" + Arrays.toString(answers.toArray()) +
                '}';
    }

    protected boolean hasOtherOwner(User owner) {
        if (answers.isEmpty()) {
            return false;
        }
        User writer = owner;
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            if (!answer.getWriter().equals(writer)) {
                return true;
            }
        }
        return false;
    }

    protected DeleteHistories deleteAll(User owner) throws CannotDeleteException {
        if (hasOtherOwner(owner)) {
            throw new CannotDeleteException("Question has the answer from other owner.");
        }

        DeleteHistories histories = new DeleteHistories();
        for (Answer answer : answers) {
            histories.addHistory(answer.delete());
        }
        return histories;
    }
}
