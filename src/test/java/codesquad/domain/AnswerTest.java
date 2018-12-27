package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;

import static codesquad.domain.QuestionTest.RED_QUESTION;
import static codesquad.domain.UserTest.RED;
import static codesquad.domain.UserTest.UNHEE;
import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {
    public static final Answer RED_ANSWER_QUESTION = new Answer(3L, RED, RED_QUESTION, "contents3");

    @Test
    public void delete() {
        RED_ANSWER_QUESTION.delete(RED);
        assertThat(RED_ANSWER_QUESTION.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_different_writer() {
        RED_ANSWER_QUESTION.delete(UNHEE);
        assertThat(RED_ANSWER_QUESTION.isDeleted()).isFalse();
    }
}
