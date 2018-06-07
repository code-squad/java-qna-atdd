package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class QuestionTest {

    public static final Question TEST_QUESTION = new Question("title", "contents");
    private Answer answer;

    @Before
    public void setup() {
        answer = new Answer(1L, UserTest.JAVAJIGI, QuestionTest.TEST_QUESTION, "testContents");
    }

    @Test
    public void update_owner() {
        TEST_QUESTION.writeBy(UserTest.JAVAJIGI);
        Question updateQuestion = new Question("title2", "contents2");
        TEST_QUESTION.update(updateQuestion, UserTest.JAVAJIGI);

        assertThat(TEST_QUESTION.getTitle(), is("title2"));
        assertThat(TEST_QUESTION.getContents(), is("contents2"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_no_owner() {
        TEST_QUESTION.writeBy(UserTest.SANJIGI);
        Question updateQuestion = new Question("title2", "contents2");
        TEST_QUESTION.update(updateQuestion, UserTest.JAVAJIGI);
    }

    @Test
    public void delete_owner() throws CannotDeleteException{
        TEST_QUESTION.writeBy(UserTest.JAVAJIGI);
        TEST_QUESTION.addAnswer(answer);
        List<DeleteHistory> deleteHistories  = TEST_QUESTION.delete(UserTest.JAVAJIGI);
        assertThat(deleteHistories.size(), is(2));
        assertThat(answer.isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_no_owner() throws CannotDeleteException{
        TEST_QUESTION.writeBy(UserTest.JAVAJIGI);
        TEST_QUESTION.addAnswer(answer);
        List<DeleteHistory> deleteHistories  = TEST_QUESTION.delete(UserTest.SANJIGI);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_no_owner_of_answer() throws CannotDeleteException{
        TEST_QUESTION.writeBy(UserTest.JAVAJIGI);
        Answer answer2 = new Answer(1L, UserTest.SANJIGI, QuestionTest.TEST_QUESTION, "testContents");
        TEST_QUESTION.addAnswer(answer);
        TEST_QUESTION.addAnswer(answer2);
        List<DeleteHistory> deleteHistories = TEST_QUESTION.delete(UserTest.SANJIGI);
    }
}