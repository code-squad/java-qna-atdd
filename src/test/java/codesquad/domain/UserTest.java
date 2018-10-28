package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.domain.UserGenerator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class UserTest {

    @Test
    public void update_owner() throws Exception {
        User origin = UserGenerator.newUser("sanjigi");
        User loginUser = origin;
        User target = new User("sanjigi", "password", "name2", "javajigi@slipp.net2");
        origin.update(loginUser, target);
        assertThat(origin.getName(), is(target.getName()));
        assertThat(origin.getEmail(), is(target.getEmail()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        User origin = UserGenerator.newUser("sanjigi");
        User loginUser = UserGenerator.newUser("javajigi");
        User target = new User("sanjigi", "password", "name2", "javajigi@slipp.net2");
        origin.update(loginUser, target);
    }

    @Test
    public void update_match_password() {
        User origin = UserGenerator.newUser("sanjigi");
        User target = new User("sanjigi", "password", "name2", "javajigi@slipp.net2");
        origin.update(origin, target);
        assertThat(origin.getName(), is(target.getName()));
        assertThat(origin.getEmail(), is(target.getEmail()));
    }

    @Test
    public void update_mismatch_password() {
        User origin = UserGenerator.newUser("sanjigi", "password");
        User target = new User("sanjigi", "password2", "name2", "javajigi@slipp.net2");
        origin.update(origin, target);
        assertThat(origin.getName(), is(not(target.getName())));
        assertThat(origin.getEmail(), is(not(target.getEmail())));
    }
}
