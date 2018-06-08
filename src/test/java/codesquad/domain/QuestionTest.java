package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {
    private static final User QUESTION_WRITER = new User("QUESTION_WRITER", "password", "name", "email");
    private static final User NOT_WRITER = new User("notWriter", "password", "content", "email");
    private Question question;

    @Before
    public void setUp() {
        question = new Question("title", "original");
        question.writeBy(QUESTION_WRITER);
    }

    @Test
    public void updateQuestion_Is_Owner() {
        Question updated = new Question("title", "updated");
        question.updateQuestion(updated, QUESTION_WRITER);
        assertThat(question.getContents(), is("updated"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateQuestion_Is_NOT_Owner() {
        Question updated = new Question("title", "updated");
        question.updateQuestion(updated, NOT_WRITER);
        assertThat(question.getContents(), is("original"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_FAIL_Is_NOT_Owner() {
        question.deleteQuestion(NOT_WRITER);
        assertThat(question.isDeleted(), is(false));
    }

    @Test
    public void deleteQuestion_SUCCESS_IsOwner_NoAnswers() {
        assertThat(question.hasAnswers(), is(false));

        question.deleteQuestion(QUESTION_WRITER);
        assertThat(question.isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestion_FAIL_IsOwner_AnswersByOthersExist() {
        Answer answer1 = new Answer("new answer");
        answer1.writeBy(NOT_WRITER);
        question.addAnswer(answer1);

        Answer answer2 = new Answer("new answer");
        answer2.writeBy(QUESTION_WRITER);
        question.addAnswer(answer2);
        assertThat(question.hasAnswers(), is(true));

        question.deleteQuestion(QUESTION_WRITER);
        assertThat(question.isDeleted(), is(false));
    }

    @Test
    public void deleteQuestion_SUCCESS_IsOwner_AllAnswersByQuestionAuthor() {
        Answer answer1 = new Answer("new answer");
        answer1.writeBy(QUESTION_WRITER);
        question.addAnswer(answer1);

        Answer answer2 = new Answer("new answer");
        answer2.writeBy(QUESTION_WRITER);
        question.addAnswer(answer2);
        assertThat(question.hasAnswers(), is(true));

        question.deleteQuestion(QUESTION_WRITER);
        assertThat(question.isDeleted(), is(true));
    }
}
