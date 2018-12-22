package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    public static Question originalQuestion = new Question("title", "contents");
    public static Question updatedQuestion = new Question("updatedTitle", "updatedContents");

    public static User owner = new User(1, "javajigi", "password", "name", "javajigi@slipp.net");
    public static User other = new User(2, "sanjigi", "password", "name", "sanjigi@slipp.net");

    @Before
    public void setUp() throws Exception {
        originalQuestion.setId(1);
        originalQuestion.writeBy(owner);
    }

    @Test
    public void update_success() throws Exception {
        originalQuestion.update(updatedQuestion, owner);
        softly.assertThat(originalQuestion.getTitle()).isEqualTo(updatedQuestion.getTitle());
        softly.assertThat(originalQuestion.getContents()).isEqualTo(updatedQuestion.getContents());
    }

    @Test(expected = UnAuthenticationException.class)   //expected하면 뭐가??
    public void update_fail() throws Exception {
        originalQuestion.update(updatedQuestion, other);
        softly.assertThat(originalQuestion.getTitle()).isNotEqualTo(updatedQuestion.getTitle());
        softly.assertThat(originalQuestion.getContents()).isNotEqualTo(updatedQuestion.getContents());
        softly.assertThat(originalQuestion.getContents()).isEqualTo(updatedQuestion.getContents());
    }

    @Test
    public void delete_success() throws Exception {
        originalQuestion.delete(owner);
        softly.assertThat(originalQuestion.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_fail() throws Exception {
        originalQuestion.delete(other);
        softly.assertThat(originalQuestion.isDeleted()).isFalse();
    }
}
