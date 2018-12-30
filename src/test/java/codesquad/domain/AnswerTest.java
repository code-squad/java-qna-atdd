package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import support.test.BaseTest;

import static codesquad.domain.QuestionTest.originalQuestion;
import static codesquad.domain.UserTest.owner;
import static codesquad.domain.UserTest.other;
import static org.slf4j.LoggerFactory.getLogger;

public class AnswerTest extends BaseTest {
    private static final Logger logger = getLogger(AnswerTest.class);

    public static Answer answer = new Answer(1L, owner, originalQuestion, "contents");

    @Before
    public void setUp() throws Exception {
        originalQuestion.setId(1);
        originalQuestion.writeBy(owner);
        originalQuestion.addAnswer(answer);
    }

    @Test
    public void isOwner() {
        softly.assertThat(answer.isOwner(owner)).isTrue();
    }

    @Test
    public void toQuestion() {
        Answer answer = new Answer(owner,"contents");
        answer.toQuestion(originalQuestion);
        softly.assertThat(answer.getQuestion()).isEqualTo(originalQuestion);
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
