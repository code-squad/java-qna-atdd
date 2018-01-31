package codesquad.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static codesquad.domain.ContentType.ANSWER;

@Getter
@NoArgsConstructor
@Embeddable
public class Answers implements Serializable {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public void add(Answer answer) {
        answers.add(answer);
    }

    public boolean isHasOtherWriter(User writer) {
        return answers.stream().filter(answer -> !answer.isOwner(writer)).findAny().isPresent();
    }

    public Collection<? extends DeletedId> convertDeletedId() {
        return answers.stream().map(Answers::apply).collect(Collectors.toList());
    }

    private static DeletedId apply(Answer answer) {
        return new DeletedId(answer.getId(), ANSWER);
    }
}
