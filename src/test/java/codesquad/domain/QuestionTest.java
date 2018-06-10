package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {
    public static User newUser(String userId, String password) {
        return new User(1L, userId, password, "name", "javajigi@slipp.net");
    }

    @Test
    public void update_owner() {
        User originUser = newUser("testid", "testpass");
        User targetUser = newUser("testid", "testpass");

        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        Question target = new Question("modified title", "modified contents");
        target.writeBy(targetUser);

        origin.update(target, originUser);
        assertThat(origin.getTitle(), is(target.getTitle()));
        assertThat(origin.getContents(), is(target.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        User originUser = newUser("testid", "testpass");
        User targetUser = newUser("testidaa", "testpass");

        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        Question target = new Question("modified title", "modified contents");
        target.writeBy(targetUser);

        origin.update(target, targetUser);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_match_password() {
        User originUser = newUser("testid", "testpass");
        User targetUser = newUser("testid", "testpassffff");

        Question origin = new Question("title", "contents");
        origin.writeBy(originUser);

        Question target = new Question("modified title", "modified contents");
        target.writeBy(targetUser);

        origin.update(target, targetUser);
    }
}
