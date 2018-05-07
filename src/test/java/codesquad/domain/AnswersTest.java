package codesquad.domain;

import codesquad.UnAuthenticationException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AnswersTest {

    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private Answers answers;


    @Before
    public void before() throws Exception {
        Answer answer1 = new Answer(JAVAJIGI,"답변이다1");
        Answer answer2 = new Answer(JAVAJIGI,"답변이다2");
        Answer answer3 = new Answer(JAVAJIGI,"답변이다3");
        Answer answer4 = new Answer(JAVAJIGI,"답변이다4");

        answers = new Answers();

        answers.addAnswer(answer1);
        answers.addAnswer(answer2);
        answers.addAnswer(answer3);
        answers.addAnswer(answer4);
    }


    @Test
    public void vaildAnswers_sucess() throws UnAuthenticationException {
        assertTrue(answers.isVaildDeleteAnswers(JAVAJIGI));
    }

    @Test
    public void vaildAnswers_fail() throws UnAuthenticationException {
        assertFalse(answers.isVaildDeleteAnswers(SANJIGI));
    }

    @Test
    public void deleteAnswers_sucess() throws UnAuthenticationException {
        List<DeleteHistory> histories =  answers.deleteAnswers(JAVAJIGI);
        assertThat(histories.size(),is(4));
    }


    @Test(expected = UnAuthenticationException.class)
    public void deleteAnswers_fail() throws UnAuthenticationException {
        List<DeleteHistory> histories = answers.deleteAnswers(SANJIGI);
        assertThat(histories.size(),is(4));
    }

    @Test
    public void isEmptyAnswersTest() {
        Answers answers2 = new Answers();
        assertFalse(answers.isEmptyAnswers());
        assertTrue(answers2.isEmptyAnswers());
    }
}
