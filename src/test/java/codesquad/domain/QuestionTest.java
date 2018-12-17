package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");


    private Question q1 = new Question (JAVAJIGI, "title1", "contents1");
    private Question q2 = new Question (SANJIGI, "title2", "contents2");
    private Question newQuestion = new Question("newTitle", "newcontents");

    @Test
    public void update_owner() {
        q1.update(JAVAJIGI, newQuestion);
        softly.assertThat(q1.getTitle()).isEqualTo("newTitle");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        q1.update(SANJIGI, newQuestion);
    }
}
