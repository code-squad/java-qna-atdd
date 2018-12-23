package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import javax.validation.constraints.Null;

public class QuestionTest extends BaseTest {
    public static final Question QUESTION_FIRST = new Question("first question title", "content");
    public static final Question QUESTION_SECOND = new Question("second question title", "new content");

    @Before
    public void setUp() throws Exception {
        QUESTION_FIRST.setId(1);
        QUESTION_SECOND.setId(2);
        QUESTION_FIRST.writeBy(UserTest.LOGIN_USER);
        QUESTION_SECOND.writeBy(UserTest.LOGIN_USER);
    }

    @Test()
    public void 질문생성_로그아웃일때() {
        Question question = QUESTION_FIRST;
        softly.assertThat(question.isOwner(null)).isFalse();
    }

    @Test
    public void 질문생성_아이디_같을때() {
        Question question = QUESTION_FIRST;
        User javajigi = UserTest.LOGIN_USER;
        softly.assertThat(question.isOwner(javajigi)).isTrue();
    }

    @Test
    public void 업데이트_로그인() {
        Question question = QUESTION_FIRST;
        Question newQuestion = QUESTION_SECOND;
        User javajigi = UserTest.LOGIN_USER;
        question.update(newQuestion, javajigi);
        softly.assertThat(question.getContents()).isEqualTo(newQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 업데이트_다른사람일때() {
        Question question = QUESTION_FIRST;
        Question newQuestion = QUESTION_SECOND;
        User another = UserTest.OTHER_USER;
        question.update(newQuestion, another);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 업데이트_로그인_안됨() {
        Question question = QUESTION_FIRST;
        Question updateQuestion = QUESTION_SECOND;
        question.update(updateQuestion, null);
    }

    @Test
    public void 삭제_로그인됨() {
        Question question = QUESTION_FIRST;
        question.delete(UserTest.LOGIN_USER);
        softly.assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = NullPointerException.class)
    public void 삭제_로그인안됨() {
        Question question = QUESTION_FIRST;
        question.delete(null);
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제_다른유저() {
        Question question = QUESTION_FIRST;
        question.delete(UserTest.OTHER_USER);
    }
}
