package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import org.junit.Before;
import org.junit.Test;

import static codesquad.domain.UserTest.newUser;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class AnswerTest {
    private static final AnswerDto UPDATED_ANSWER = new AnswerDto("updated answer");
    private Question question;
    private User writer;
    private User temporal;
    private Answer answer;

    @Before
    public void init() {
        writer = newUser("sanjigi");
        temporal = newUser("temporal");

        question = new Question("title", "contents");
        question.writeBy(writer);

        answer = new Answer(writer, "answer test");
    }

    @Test
    public void updateTest() {
        answer.update(writer, UPDATED_ANSWER);
        assertThat(answer.getContents()).isEqualTo(UPDATED_ANSWER.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateTest_with_other() {
        answer.update(temporal, UPDATED_ANSWER);
    }

    @Test
    public void deleteTest() {
        answer.delete(writer);
        assertTrue(answer.isDeleted());
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteTest_with_other() {
        answer.delete(temporal);
    }
}
