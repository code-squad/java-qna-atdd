package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnswerTest {
    private static final User WRITER = new User("writer", "password", "name", "email");
    private static final User NOT_WRITER = new User("notWriter", "password", "name", "email");

    private Answer answer;

    @Before
    public void setUp() {
        answer = new Answer("content");
        answer.writeBy(WRITER);
    }

    @Test
    public void deleteAnswer_is_owner() {
        answer.deleteAnswerByOwner(WRITER);
        assertTrue(answer.isDeleted());
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAnswer_is_NOT_owner() {
        answer.deleteAnswerByOwner(NOT_WRITER);
        assertFalse(answer.isDeleted());
    }
}
