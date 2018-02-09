package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AnswerTest {

    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private Question question;
    private Answer answer;

    @Before
    public void setup() {
        question = new Question("title", "contents");
        question.writeBy(JAVAJIGI);
        answer = new Answer(JAVAJIGI, question, "answer contents");
    }

    @Test
    public void writeBy_for_create() {
        assertThat(answer.isOwner(JAVAJIGI), is(true));
        assertThat(answer.isOwner(SANJIGI), is(false));
    }

    @Test (expected = UnAuthorizedException.class)
    public void update_not_owner() {
        answer.update(SANJIGI, answer);
    }

    @Test
    public void update_owner() {
        String updateContents = "updated contents";
        Answer updatedAnswer = new Answer(JAVAJIGI, question, updateContents);
        answer.update(JAVAJIGI, updatedAnswer);

        assertThat(answer.getContents(), is(updateContents));
    }
}
