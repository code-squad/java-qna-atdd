package codesquad.service;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import support.test.BaseTest;

import java.util.Optional;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest extends BaseTest {
    @Mock       //db가 없어도 테스트 가능 하도록 ( db에 의존하지 않도록)
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void login_success() throws Exception {
        User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
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
}
