package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {
    public static final User LEARNER = new User(1L, "learner", "9229", "TAEWON", "htw@gmail.com");
    public static final User POBI = new User(2L, "pobi", "1234", "jaesung", "pjs@gmail.com");
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

    @Test
    public void add_answer() {
        Answer answer = new Answer(1L, LEARNER, null, "첫번 째 질문 댓글");
        firstQuestion.addAnswer(answer);

        assertThat(answer.getQuestion().equals(firstQuestion), is(true));
    }
}
