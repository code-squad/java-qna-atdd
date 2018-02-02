package codesquad.domain;

import codesquad.CannotDeleteException;
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

    public boolean isAnswersDeletable(User writer) {
        return answers.stream()
                .allMatch(a -> a.isDeletable(writer));
    }

    public List<DeleteHistory> deleteAnswers(User writer) throws CannotDeleteException {
        if(!this.isAnswersDeletable(writer))
            throw new CannotDeleteException("Answers can't be deleted");

        return answers.stream()
                .map(a -> a.delete(writer))
                .collect(Collectors.toList());
    }

    public void add(Answer answer) {
        answers.add(answer);
    }
}
