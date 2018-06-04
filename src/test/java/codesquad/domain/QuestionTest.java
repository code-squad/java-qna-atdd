package codesquad.domain;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;

public class QuestionTest {

    private Question question;
    private User javajigi;
    private User sanjigi;

    @Before
    public void setup() {
        question = new Question("title", "contents");
        javajigi = new User("javajigi", "password", "name", "javajigi@slipp.net");
        sanjigi = new User("sanjigi", "password", "name", "javajigi@slipp.net");
    }

    @Test
    public void deletable_true() {
        question.writeBy(javajigi);
        question.addAnswer(new Answer(javajigi, "hello"));
        assertTrue(question.isDeletable(javajigi));
    }

    @Test
    public void deletable_false_owner_not_match() {
        question.writeBy(javajigi);

        assertFalse(question.isDeletable(sanjigi));
    }

    @Test
    public void deletable_false_answer_owner_not_match() {
        question.writeBy(javajigi);
        question.addAnswer(new Answer(sanjigi, "hello2"));
        assertFalse(question.isDeletable(javajigi));
    }


}
