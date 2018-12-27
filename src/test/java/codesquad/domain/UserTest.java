package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class UserTest extends BaseTest {
    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    public static User newUser(Long id) {
        return new User(id, "userId", "pass", "name", "javajigi@slipp.net");
    }

    public static User newUser(String userId) {
        return newUser(userId, "password");
    }

    public static User newUser(String userId, String password) {
        return new User(0L, userId, password, "name", "javajigi@slipp.net");
    }

    @Test
    public void update_owner() throws Exception {
        User origin = newUser("sanjigi");
        User loginUser = origin;
        User target = new User("sanjigi", "password", "name2", "javajigi@slipp.net2");
        origin.update(loginUser, target);
        softly.assertThat(origin.getName()).isEqualTo(target.getName());
        softly.assertThat(origin.getEmail()).isEqualTo(target.getEmail());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        User origin = newUser("sanjigi");
        User loginUser = newUser("javajigi");
        User target = new User("sanjigi", "password", "name2", "javajigi@slipp.net2");
        origin.update(loginUser, target);       //sanjigi가 javajigi의 개인정보를 sanjigi로 바꾸려고 한다.
    }

    @Test
    public void update() {
        User origin = newUser("sanjigi");
        User target = new User("sanjigi", "password", "name2", "javajigi@slipp.net2");
        origin.update(origin, target);
        softly.assertThat(origin.getName()).isEqualTo(target.getName());
        softly.assertThat(origin.getEmail()).isEqualTo(target.getEmail());
    }

}
