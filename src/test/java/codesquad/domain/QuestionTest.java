package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import java.util.Arrays;

import static codesquad.domain.UserTest.RED;
import static codesquad.domain.UserTest.UNHEE;

public class QuestionTest extends BaseTest {
    public static final Question RED_QUESTION = new Question(RED, "title3", "contents3");
    public static final Question UNHEE_QUESTION = new Question(UNHEE, "title", "contents");
    private Question newQuestion = new Question("newTitle", "newcontents");

    @Before
    public void setUp() throws Exception {
        UNHEE_QUESTION.setAnswers(Arrays.asList(new Answer(UNHEE, "answer contents")));
    }

    @Test
    public void update_owner() {
        RED_QUESTION.update(RED, newQuestion);
        softly.assertThat(RED_QUESTION.getTitle()).isEqualTo("newTitle");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        RED_QUESTION.update(UNHEE, newQuestion);
    }

    @Test
    public void delete_no_answer() throws CannotDeleteException {
        RED_QUESTION.delete(RED);
        softly.assertThat(RED_QUESTION.isDeleted()).isTrue();
    }

    @Test
    public void delete_owner_answer() throws CannotDeleteException {
        UNHEE_QUESTION.delete(UNHEE);
        softly.assertThat(UNHEE_QUESTION.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_writer_other_answer() throws CannotDeleteException {
        UNHEE_QUESTION.delete(RED);
        softly.assertThat(UNHEE_QUESTION.isDeleted()).isFalse();
    }
}
