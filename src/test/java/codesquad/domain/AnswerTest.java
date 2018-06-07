package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class AnswerTest {

    private Answer answer;

    @Before
    public void setup() {
        answer = new Answer(1L, UserTest.JAVAJIGI, QuestionTest.TEST_QUESTION, "testContents");
    }

    @Test
    public void update() {
        answer.update(UserTest.JAVAJIGI, "updateContents");
        assertThat(answer.getContents(), is("updateContents"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_다른_사용자() {
        answer.update(UserTest.SANJIGI, "updateContents");
    }

    @Test
    public void delete() throws CannotDeleteException {
        DeleteHistory deleteHistory = answer.delete(UserTest.JAVAJIGI);
        assertThat(answer.isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_다른_사용자() throws CannotDeleteException {
        DeleteHistory deleteHistory = answer.delete(UserTest.SANJIGI);
    }
}
