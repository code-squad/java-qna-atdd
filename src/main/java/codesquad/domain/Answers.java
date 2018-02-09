package codesquad.domain;

import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoon on 2018. 2. 9..
 */
@Embeddable
public class Answers {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public List<Answer> add(Answer answer) {
        answers.add(answer);
        return answers;
    }

    public Integer size() {
        return answers.size();
    }

    public Boolean isAllSameOwner(User loginUser) {
        return answers.stream().allMatch(answer -> answer.isOwner(loginUser));
    }

    public Boolean isAllDeleted() {
        return answers.stream().allMatch(Answer::isDeleted);
    }

    public void delete(User loginUser) {
        answers.forEach(answer -> answer.delete(loginUser));
    }

    public List<Answer> getAll() {
        return answers;
    }
}
