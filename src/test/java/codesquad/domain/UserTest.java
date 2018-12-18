package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

public class UserTest extends BaseTest {
    public static final User BRAD = new User(1L, "brad903", "1234", "브래드", "brad903@naver.com");
    public static final User JUNGHYUN = new User(2L, "leejh903", "1234", "이정현", "leejh903@gmail.com");

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
        User origin = BRAD;
        User loginUser = origin;
        User target = new User("brad903", "1234", "newName_Brad", "brad999@naver.com");
        origin.update(loginUser, target);
        softly.assertThat(origin.getName()).isEqualTo(target.getName());
        softly.assertThat(origin.getEmail()).isEqualTo(target.getEmail());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() throws Exception {
        User origin = BRAD;
        User loginUser = JUNGHYUN;
        User target = new User("brad903", "1234", "newName_Brad", "brad999@naver.com");
        origin.update(loginUser, target);
    }

    @Test
    public void update_match_password() {
        User origin = BRAD;
        User target = new User("brad903", "1234", "newName_Brad", "brad999@naver.com");
        origin.update(origin, target);
        softly.assertThat(origin.getName()).isEqualTo(target.getName());
        softly.assertThat(origin.getEmail()).isEqualTo(target.getEmail());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_mismatch_password() {
        User origin = BRAD;
        User target = new User("brad903", "12345", "newName_Brad", "brad999@naver.com");
        origin.update(origin, target);
    }
}
