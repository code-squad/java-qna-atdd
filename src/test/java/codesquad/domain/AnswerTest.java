package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.BaseTest;

import static codesquad.domain.UserTest.other;
import static codesquad.domain.UserTest.user;

public class AnswerTest extends BaseTest {
    private static final Logger log = LoggerFactory.getLogger(AnswerTest.class);
    public static final String ANSWER = "answer";
    public static final String UPDATED_ANSWER = "updatedAnswer";

    public static Answer answer = new Answer(user, ANSWER);

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        answer.update(other, UPDATED_ANSWER);
    }

    @Test
    public void update() {
        Answer updatedAnswer = answer.update(user, UPDATED_ANSWER);

        softly.assertThat(updatedAnswer.isOwner(user)).isEqualTo(true);
        softly.assertThat(updatedAnswer.getContents()).isEqualTo(UPDATED_ANSWER);
    }
}
