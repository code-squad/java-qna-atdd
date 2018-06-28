package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {
    private static final Logger log =  LoggerFactory.getLogger(QuestionTest.class);
    private static final User LEARNER = new User(1L, "learner", "9229", "TAEWON", "htw@gmail.com");
    private static final User POBI = new User(2L, "pobi", "1234", "jaesung", "pjs@gmail.com");
    private Question firstQuestion;
    private Question secondQuestion;
    private Question updateQuestion;

    @Before
    public void setUp() {
        firstQuestion = new Question(1L, "1번 질문 제목", "1번 질문 내용");
        firstQuestion.writeBy(LEARNER);
        secondQuestion = new Question(2L, "2번 질문 제목", "2번 질문 내용");
        updateQuestion = new Question("수정된 질문 제목", "수정된 질문 내용");
    }

    @Test
    public void update_owner() {
        firstQuestion.update(LEARNER, updateQuestion);

        assertThat(firstQuestion.getTitle(),is(updateQuestion.getTitle()));
        assertThat(firstQuestion.getContents(),is(updateQuestion.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        firstQuestion.update(POBI, updateQuestion);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        firstQuestion.delete(LEARNER);
        assertThat(firstQuestion.isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_onwer() throws CannotDeleteException {
        firstQuestion.delete(POBI);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_my_answer() throws CannotDeleteException {
        Question question = new Question("TDD를 이을 PDD는 무엇인가요?", "Pobi Driven Development !");
        question.writeBy(LEARNER);

        Answer answer = new Answer(POBI, "PDD는 매우 강력하죠, 그래서 많이 배웁니다!");
        question.addAnswer(answer);

        question.delete(LEARNER);
        assertThat(question.isDeleted(), is(false));
    }

    @Test
    public void delete_only_my_answer() throws CannotDeleteException {
        Question question = new Question("최근들어 제 부족한 모습을 몇 번씩이나 발견했습니다.", "아는 척보다는 모르는 척이 낫다.");
        question.writeBy(LEARNER);

        Answer answer = new Answer(LEARNER, "겸손의 자세를 유지하자.");
        question.addAnswer(answer);

        question.delete(LEARNER);
        assertThat(question.isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_exist_question() throws CannotDeleteException {
        Question question = new Question("System.out.println에 전달된 문자열이 화면에 띄워지기 까지는?", "한 번 고민해보면 좋은 주제!");
        question.writeBy(LEARNER);

        question.delete(LEARNER);
        // 논리적으로 지워진 질문(사용자입장에서는 없는 질문)을 다시 지우려고 할 때 예외 발생
        question.delete(LEARNER);
    }

    @Test
    public void addDeleteHistory() throws CannotDeleteException {
        Question question = new Question("의식적인 연습, 의식적인 노력이 필요할까요?", "속도도 중요하지만, 속도만큼 중요한 것이 방향.");
        question.writeBy(LEARNER);

        Answer answer = new Answer(LEARNER, "겸손한 자세를 유지할 수 있는 의식적인 연습을 하자.");
        question.addAnswer(answer);

        DeleteHistories histories = question.delete(LEARNER);

        assertThat(question.isDeleted(), is(true));
        assertThat(histories.size() > 0, is(true));
    }

    @Test
    public void add_answer() {
        Answer answer = new Answer(1L, LEARNER, null, "첫번 째 질문 댓글");
        firstQuestion.addAnswer(answer);

        assertThat(answer.getQuestion().equals(firstQuestion), is(true));
    }
}
