package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static codesquad.domain.UserTest.RED;
import static codesquad.domain.UserTest.UNHEE;

public class QuestionTest extends BaseTest {
    public static final Question RED_QUESTION = new Question(RED, "title3", "contents3");
    public static final Question UNHEE_QUESTION = new Question(UNHEE, "title", "contents");
    private Question newQuestion = new Question("newTitle", "newcontents");

    @Test
    public void update_owner() {
        RED_QUESTION.update(RED, newQuestion);
        softly.assertThat(RED_QUESTION.getTitle()).isEqualTo("newTitle");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        RED_QUESTION.update(UNHEE, newQuestion);
    }
}
