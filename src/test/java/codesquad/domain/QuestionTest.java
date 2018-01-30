package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static codesquad.domain.UserTest.newUser;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuestionTest {
    private Question question;
    private User writer;
    private User temporal;

    @Before
    public void init() {
        writer = newUser("sanjigi");
        temporal = newUser("temporal");

        question = new Question("title", "contents");
        question.writeBy(writer);
    }

    @Test
    public void updateTest() {
        question.update(writer, new Question("updated", "updated"));
        assertThat(question.getTitle()).isEqualTo("updated");
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateTest_with_other() {
        question.update(temporal, new Question("updated", "updated"));
    }

    @Test
    public void deleteTest() throws CannotDeleteException {
        question.delete(writer);
        assertTrue(question.isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteTest_with_other() throws CannotDeleteException {
        question.delete(temporal);
    }
}
