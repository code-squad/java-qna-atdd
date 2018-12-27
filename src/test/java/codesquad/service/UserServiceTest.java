package codesquad.service;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.domain.UserTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import support.test.BaseTest;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest extends BaseTest {
    private static final Logger log = getLogger(UserServiceTest.class);

    @Mock       //db가 없어도 테스트 가능 하도록 ( db에 의존하지 않도록)
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void login_success() throws Exception {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        when(userRepository.findByUserId("sanjigi")).thenReturn(Optional.of(user));
        User loginUser = userService.login(user.getUserId(), user.getPassword());
        softly.assertThat(loginUser).isEqualTo(user);

    }

    @Test(expected = UnAuthenticationException.class)
    public void login_failed_when_user_not_found() throws Exception {
        when(userRepository.findByUserId("sanjigi")).thenReturn(Optional.empty());
        userService.login("javajigi", "password");
    }

    @Test(expected = UnAuthenticationException.class)
    public void login_failed_when_mismatch_password() throws Exception {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
        userService.login(user.getUserId(), user.getPassword() + "2");
    }

    @Test(expected = UnAuthenticationException.class)
    public void test() throws UnAuthenticationException {
        Optional.empty().orElseThrow(UnAuthenticationException::new);
    }

    @Test
    public void update() {
        User user = new User("javajigi", "password", "name", "asdf@spii.net");
        User targetUser = new User(user.getUserId(), "password2", "name2", "asdf2@spii.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        User updatedUser = userService.update(user, user.getId(), targetUser);
        softly.assertThat(updatedUser.getName()).isEqualTo("name2");
    }

    @Test(expected = UnAuthenticationException.class)
    public void update_no_login() {
        User user = UserTest.JAVAJIGI;
        User targetUser = new User(user.getUserId(), "password2", "name2", "asdf@slipp.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.update(null, user.getId(), targetUser);
    }

    @Test (expected = UnAuthorizedException.class)
    public void update_other_user() {
        User user = UserTest.JAVAJIGI;
        User targetUser = new User(user.getUserId(), "password2", "name2", "asdf2@slipp.net");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        userService.update(UserTest.SANJIGI,user.getId(),targetUser);
    }
}
