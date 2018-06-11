package codesquad.domain;

import codesquad.dto.QuestionDto;
import codesquad.exceptions.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {

    private Question question;
    private User javajigi;
    private User sanjigi;

    @Before
    public void setup() {
        question = new Question("title", "contents");
        javajigi = new User("javajigi", "password", "name", "javajigi@slipp.net");
        sanjigi = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        question.writeBy(javajigi);
    }

    @Test
    public void deletable_true_no_answer() {
        assertTrue(question.isDeletable(javajigi));
    }

    @Test
    public void deletable_false_owner_not_match() {
        assertFalse(question.isDeletable(sanjigi));
    }

    @Test
    public void deletable_true_owner_answer() {
        question.addAnswer(new Answer(javajigi, "hello"));
        assertTrue(question.isDeletable(javajigi));
    }

    @Test
    public void deletable_false_answer_owner_not_match() {
        question.addAnswer(new Answer(sanjigi, "hello2"));
        assertFalse(question.isDeletable(javajigi));
    }

    @Test
    public void update_success() {
        QuestionDto updateQuestion = new QuestionDto("change", "con");
        question.update(javajigi, updateQuestion);
        assertThat(question.getTitle(), is("change"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_fail() {
        QuestionDto updateQuestion = new QuestionDto("change", "con");
        question.update(sanjigi, updateQuestion);
    }

    @Test
    public void toDeleteHistories() {
        Answer answer1 = new Answer(javajigi, "hello");
        Answer answer2 = new Answer(javajigi, "bye");

        List<DeleteHistory> deleteHistories = new ArrayList<>();

        deleteHistories.add(new DeleteHistory(ContentType.QUESTION, question.getId(), javajigi));
        deleteHistories.add(new DeleteHistory(ContentType.ANSWER, answer1.getId(), javajigi));
        deleteHistories.add(new DeleteHistory(ContentType.ANSWER, answer2.getId(), javajigi));

        question.addAnswer(answer1);
        question.addAnswer(answer2);

        assertThat(question.toDeleteHistories(javajigi), is(deleteHistories));
    }

}
