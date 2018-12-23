package codesquad.domain;


import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {

    public static final Answer ANSWER_FIRST = new Answer(1L, UserTest.LOGIN_USER, QuestionTest.QUESTION_FIRST, "first answer");
    public static final Answer ANSWER_SECOND = new Answer(2L, UserTest.LOGIN_USER, QuestionTest.QUESTION_FIRST, "second answer");

    @Before
    public void setUp() throws Exception {
        QuestionTest.QUESTION_FIRST.setId(1);
        QuestionTest.QUESTION_SECOND.setId(2);
        QuestionTest.QUESTION_FIRST.writeBy(UserTest.LOGIN_USER);
        QuestionTest.QUESTION_SECOND.writeBy(UserTest.LOGIN_USER);
    }

    @Test
    public void 답변_로그인_생성() {
        Answer answer = ANSWER_FIRST;
        User loginUser = UserTest.LOGIN_USER;
        softly.assertThat(answer.isOwner(loginUser)).isTrue();
    }

    @Test
    public void 답변_로그인_안됨() {
        Answer answer = ANSWER_FIRST;
        softly.assertThat(answer.isOwner(null)).isFalse();
    }

    @Test
    public void 답변_업데이트_로그인() {
        Answer answer = ANSWER_FIRST;
        answer.update(ANSWER_SECOND.getContents(), UserTest.LOGIN_USER);
        softly.assertThat(answer.getContents()).isEqualTo(ANSWER_SECOND.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_업데이트_다른유저() {
        Answer answer = ANSWER_FIRST;
        answer.update(ANSWER_SECOND.getContents(), UserTest.OTHER_USER);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_업데이트_로그인안됨() {
        Answer answer = ANSWER_FIRST;
        answer.update(ANSWER_SECOND.getContents(), null);
    }

    @Test
    public void 답변_삭제_로그인됨() {
        Answer answer = ANSWER_FIRST;
        answer.delete(UserTest.LOGIN_USER);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변_삭제_다른유저() {
        Answer answer = ANSWER_FIRST;
        answer.delete(UserTest.OTHER_USER);
    }

    @Test(expected = NullPointerException.class)
    public void 답변_삭제_로그인안됨() {
        Answer answer = ANSWER_FIRST;
        answer.delete(null);
    }
}
