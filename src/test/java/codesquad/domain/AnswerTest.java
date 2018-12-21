package codesquad.domain;


import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class AnswerTest extends BaseTest {

    public static final User LOGIN_USER = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User OTHER_USER = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    public static final Question QUESTION_FIRST = new Question("first question title", "content");
    public static final Question QUESTION_SECOND = new Question("second question title", "new content");

    public static final Answer ANSWER_FIRST = new Answer(1L, LOGIN_USER, QUESTION_FIRST, "first answer");
    public static final Answer ANSWER_SECOND = new Answer(2L, LOGIN_USER, QUESTION_FIRST, "second answer");

    @Before
    public void setUp() throws Exception {
        QUESTION_FIRST.setId(1);
        QUESTION_SECOND.setId(2);
        QUESTION_FIRST.writeBy(LOGIN_USER);
        QUESTION_SECOND.writeBy(LOGIN_USER);
    }

    @Test
    public void 답변_로그인_생성() {
        Answer answer = ANSWER_FIRST;
        User loginUser = LOGIN_USER;
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
        answer.update(ANSWER_SECOND.getContents(), LOGIN_USER);
        softly.assertThat(answer.getContents()).isEqualTo(ANSWER_SECOND.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_업데이트_다른유저() {
        Answer answer = ANSWER_FIRST;
        answer.update(ANSWER_SECOND.getContents(), OTHER_USER);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 답변_업데이트_로그인안됨() {
        Answer answer = ANSWER_FIRST;
        answer.update(ANSWER_SECOND.getContents(), null);
    }

}
