package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static codesquad.domain.UserTest.BRAD;
import static codesquad.domain.UserTest.JUNGHYUN;

public class AnswerTest extends BaseTest {
    public static final Answer ANSWER = new Answer(BRAD, "답변 내용 테스트1");

    @Test
    public void update() {
        Answer updatedAnswer = ANSWER.update(BRAD, "답변 내용 업데이트");
        softly.assertThat(updatedAnswer.isOwner(BRAD)).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_other_user() {
        ANSWER.update(JUNGHYUN, "답변 내용 업데이트");
    }
}