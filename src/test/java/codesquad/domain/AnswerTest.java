package codesquad.domain;

import codesquad.CannotDeleteException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AnswerTest {
    @Test
    public void delete() throws CannotDeleteException {
        User user = new User("test", "test", "test", "test@test.com");
        Answer toDeleteAnswer = new Answer(user, "comment");
        DeleteHistory deleteHistory = new DeleteHistory(ContentType.ANSWER, toDeleteAnswer.getId(), toDeleteAnswer.getWriter());
        assertThat(toDeleteAnswer.delete(user), is(deleteHistory));
        assertTrue(toDeleteAnswer.isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_false() throws CannotDeleteException {
        User user = new User("test", "test", "test", "test@test.com");
        Answer toDeleteAnswer = new Answer(user, "comment");
        toDeleteAnswer.delete(user);
        toDeleteAnswer.delete(user);
    }
}
