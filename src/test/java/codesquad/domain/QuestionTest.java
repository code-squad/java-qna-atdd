package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class QuestionTest {

    public static final Question TEST_QUESTION = new Question("title", "contents");

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
}