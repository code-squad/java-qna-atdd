package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {
    private static final User WRITER = new User("WRITER", "password", "name", "email");
    private static final User NOT_WRITER = new User("notWriter", "password", "content", "email");
    private Question question;
    private User loginUser;

    @Before
    public void setUp() {
        question = new Question("title", "original");
        question.writeBy(WRITER);
    }

    @Test
    public void updateQuestion_Is_Owner() {
        loginUser = WRITER;
        Question updated = new Question("title", "updated");
        question.updateQuestion(updated, loginUser);
        assertThat(question.getContents(), is("updated"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateQuestion_Is_NOT_Owner() {
        loginUser = NOT_WRITER;
        Question updated = new Question("title", "updated");
        question.updateQuestion(updated, loginUser);
        assertThat(question.getContents(), is("original"));
    }

    @Test
    public void deleteQuestion_Is_Owner() {
        loginUser = WRITER;
        question.deleteQuestion(loginUser);
        assertThat(question.isDeleted(), is(true));
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_Is_NOT_Owner() {
        loginUser = NOT_WRITER;
        question.deleteQuestion(loginUser);
        assertThat(question.isDeleted(), is(false));
    }
}
