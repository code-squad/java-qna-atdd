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
    private List<Answer> answers;

    public Answers() {
        answers = new ArrayList<>();
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    public boolean isEmpty() {
        return answers.isEmpty();
    }

    public boolean checkAllWriterSameWith(User writer) {
        for (Answer answer: answers) {
            if (!answer.isOwner(writer)) {
                return false;
            }
        }

        return true;
    }

    public void delete(List<DeleteHistory> toReturnList, User writer) throws CannotDeleteException {
        for (Answer answer: answers) {
            toReturnList.add(answer.delete(writer));
        }
    }
}
