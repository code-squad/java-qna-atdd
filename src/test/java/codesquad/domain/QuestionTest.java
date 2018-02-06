package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class QuestionTest {

    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    private Question question;

    @Before
    public void setup() {
        question = new Question("title", "contents");
    }

    @Test (expected = IllegalArgumentException.class)
    public void nothing_created_without_user() {
        new Question(null, new QuestionDto("test","testest"));
    }

    @Test
    public void create_by_user() {
        new Question(JAVAJIGI, new QuestionDto("test", "testest"));
    }

    @Test (expected = UnAuthorizedException.class)
    public void updated_by_not_owner() {
        question.update(null, question);
    }

    @Test (expected = UnAuthorizedException.class)
    public void updated_by_different_owner() {
        question.writeBy(JAVAJIGI);
        question.update(SANJIGI, question);
    }

    @Test
    public void updated_by_owner() {
        question.writeBy(JAVAJIGI);
        Question updatedQuestion = new Question("updated title", "updated contents");
        updatedQuestion = question.update(JAVAJIGI, updatedQuestion);

        assertTrue(question.equals(updatedQuestion));
    }
}
