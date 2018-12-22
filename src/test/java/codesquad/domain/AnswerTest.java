package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import support.test.BaseTest;

import static org.slf4j.LoggerFactory.getLogger;

public class AnswerTest extends BaseTest {
    private static final Logger logger = getLogger(AnswerTest.class);

    private static Question question = new Question("title", "contents");
    private static User owner = new User(0, "owner", "test", "owner", "owner@gmail.com");
    private static User other = new User(1, "other", "test", "other", "other@gmail.com");
    private static Answer answer = new Answer(0L, owner, question, "contents");

    @Before
    public void setUp() throws Exception {
        question.setId(1);
        question.writeBy(owner);
        question.addAnswer(answer);
    }

    @Test
    public void isOwner() {
        softly.assertThat(answer.isOwner(owner)).isTrue();
    }

    @Test
    public void toQuestion() {
        Answer answer = new Answer(owner,"contents");
        answer.toQuestion(question);
        softly.assertThat(answer.getQuestion()).isEqualTo(question);
    }

    @Test
    public void delete() {
        answer.delete(owner);
        softly.assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_other() {
        answer.delete(other);
    }
}
