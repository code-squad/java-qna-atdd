package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static codesquad.domain.UserTest.newUser;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

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

    @Test
    public void deleteTest_with_answer() throws CannotDeleteException {
        Answer answer = new Answer(writer, "1");
        question.addAnswer(answer);
        
        List<DeleteHistory> histories = question.delete(writer);

        assertTrue(question.isDeleted());
        assertThat(histories.get(0)).isEqualTo(answer.delete(writer));
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteTest_with_other() throws CannotDeleteException {
        question.delete(temporal);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteTest_with_answer_written_by_other() throws CannotDeleteException {
        question.addAnswer(new Answer(temporal, "1"));
        question.delete(writer);
    }
}
