package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import javax.validation.constraints.Null;

public class QuestionTest extends BaseTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    public static final Question QUESTION_FIRST = new Question("first question title", "content");
    public static final Question QUESTION_SECOND = new Question("second question title", "new content");

    @Before
    public void setUp() throws Exception {
        QUESTION_FIRST.setId(1);
        QUESTION_SECOND.setId(2);
        QUESTION_FIRST.writeBy(JAVAJIGI);
        QUESTION_SECOND.writeBy(JAVAJIGI);
    }

    @Test()
    public void 질문생성_로그아웃일때() {
        Question question = QUESTION_FIRST;
        softly.assertThat(question.isOwner(null)).isFalse();
    }

    @Test
    public void 질문생성_아이디_같을때() {
        Question question = QUESTION_FIRST;
        User javajigi = JAVAJIGI;
        softly.assertThat(question.isOwner(javajigi)).isTrue();
    }

    @Test
    public void 업데이트_로그인() {
        Question question = QUESTION_FIRST;
        Question newQuestion = QUESTION_SECOND;
        User javajigi = JAVAJIGI;
        question.update(newQuestion, javajigi);
        softly.assertThat(question.getContents()).isEqualTo(newQuestion.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void 업데이트_다른사람일때() {
        Question question = QUESTION_FIRST;
        Question newQuestion = QUESTION_SECOND;
        User another = SANJIGI;
        question.update(newQuestion, another);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 업데이트_로그인_안됨() {
        Question question = QUESTION_FIRST;
        Question updateQuestion = QUESTION_SECOND;
        question.update(updateQuestion, null);
    }
}
