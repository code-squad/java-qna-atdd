package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.CannotUpdateException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AnswerTest {
    public static final User SSOSSO = new User(1L, "ssosso", "password", "name", "ssossohow@gmail.com");

    public static final User QUINDICI = new User(2L, "quindici", "password", "name", "quindici@gmail.com");

    @Test
    public void answer_owner_check() {
        Answer answer = new Answer(SSOSSO, "내용");
        assertTrue(answer.isOwner(SSOSSO));
        assertFalse(answer.isOwner(QUINDICI));
    }

    @Test
    public void update_owner() throws Exception {
        Answer answer = new Answer(SSOSSO, "내용");
        Answer updateAnswer = new Answer("내용1");
        answer.update(SSOSSO, updateAnswer);
        assertThat(answer.getContents(), is(updateAnswer.getContents()));
    }

    @Test(expected = CannotUpdateException.class)
    public void update_not_owner() throws Exception {
        Answer answer = new Answer(SSOSSO, "내용");
        Answer updateAnswer = new Answer("내용1");
        answer.update(QUINDICI, updateAnswer);
    }

    @Test
    public void delete_owner() throws Exception {
        Answer answer = new Answer(SSOSSO, "내용");
        answer.delete(SSOSSO);
        assertTrue(answer.isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_owner() throws Exception {
        Answer answer = new Answer(SSOSSO, "내용");
        answer.delete(QUINDICI);
    }
}
