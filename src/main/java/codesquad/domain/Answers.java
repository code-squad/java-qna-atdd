package codesquad.domain;

import codesquad.UnAuthenticationException;
import codesquad.dto.AnswerDto;
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

    public Answers() {}

    void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public List<AnswerDto> getAnswersDtoes() {
        List<AnswerDto> answerDtoList = new ArrayList<AnswerDto>();
        for(Answer answer : answers) {
            answerDtoList.add(answer.toAnswerDto());
        }
        return answerDtoList;
    }

    public List<DeleteHistory> deleteAnswers(User deleteUser) throws UnAuthenticationException {

        List<DeleteHistory> histories = new ArrayList<>();

        if(isEmptyAnswers()) {
            return histories;
        }

        if(isVaildDeleteAnswers(deleteUser)) {
            for (Answer answer : answers) {
                answer.delete(deleteUser);
                histories.add(new DeleteHistory(ContentType.ANSWER, 0l, deleteUser, LocalDateTime.now()));
            }
        }

        return histories;
    }

    boolean isVaildDeleteAnswers(User deleteUser) throws UnAuthenticationException {

        for(Answer answer:answers) {
            if(!answer.isAnswerOwner(deleteUser)) {
                throw new UnAuthenticationException("삭제자와 답변자가 일치하지 않습니다.");
            }
        }

        return true;
    }

    boolean isEmptyAnswers() {

        return answers.size() == 0;
    }
}
