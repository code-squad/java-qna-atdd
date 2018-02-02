package codesquad.domain;

import codesquad.CannotDeleteException;
import org.junit.Before;
import org.junit.Test;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;

public class AnswersTest {
    private Answers answers;

    @Before
    public void init() {
        answers = new Answers();

        answers.add(new Answer(SANJIGI, "1"));
        answers.add(new Answer(SANJIGI, "1"));
    }

    @Test
    public void deletableTest() throws Exception {
        assertTrue(answers.isAnswersDeletable(SANJIGI));
    }

    @Test
    public void deletableTest_with_other() throws Exception {
        assertFalse(answers.isAnswersDeletable(JAVAJIGI));
    }

    @Test
    public void deleteTest() throws Exception {
        assertThat(answers.deleteAnswers(SANJIGI).size()).isEqualTo(2);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteTest_with_other() throws Exception {
        answers.deleteAnswers(JAVAJIGI);
    }
}
