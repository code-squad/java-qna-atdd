package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class QuestionTest {

    private Question question;
    private User user1;
    private User user2;

    @Before
    public void setUp() throws Exception {
        user1 = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
        user2 = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
        question = new Question("title", "content");
        question.writeBy(user1);
    }

    @Test
    public void isOwner() {
        assertThat(question.isOwner(user1)).isTrue();
        assertThat(question.isOwner(user2)).isFalse();
    }

    @Test
    public void update() {
        question.update(user1, new Question("update", "update"));
        assertThat(question.getTitle()).isEqualTo("update");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_unauthorized() {
        question.update(user2, new Question("update", "update"));
    }

    @Test
    public void delete() {
        question.delete(user1);
        assertThat(question.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_unauthorized() {
        question.delete(user2);

    }
}
