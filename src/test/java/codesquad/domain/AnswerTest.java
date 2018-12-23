package codesquad.domain;

import codesquad.CannotDeleteException;
import org.junit.Test;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.CHOI;
import static codesquad.domain.UserTest.SING;
import static org.junit.Assert.*;

public class AnswerTest extends AcceptanceTest {

    public static final Answer NEW_ANSWER = new Answer(CHOI, "newContents");
    public static final Answer DELETE_ANSWER = new Answer(CHOI, "deleteAnswerContents");
    public static final Answer DELETE_ANSWER_SECOND = new Answer(CHOI, "deleteAnswerContents");

    @Test
    public void delete() throws CannotDeleteException {
        DELETE_ANSWER_SECOND.delete(CHOI);
        softly.assertThat(DELETE_ANSWER_SECOND.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteWithInvalidUser() throws CannotDeleteException {
        NEW_ANSWER.delete(SING);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteWithoutLogin() throws CannotDeleteException {
        NEW_ANSWER.delete(null);
    }

}