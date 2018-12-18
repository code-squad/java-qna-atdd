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

import static codesquad.domain.UserTest.BRAD;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest extends BaseTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void login_success() throws Exception {
        when(userRepository.findByUserId(BRAD.getUserId())).thenReturn(Optional.of(BRAD));

        User loginUser = userService.login(BRAD.getUserId(), BRAD.getPassword());
        softly.assertThat(loginUser).isEqualTo(BRAD);
    }

    @Test(expected = UnAuthenticationException.class)
    public void login_failed_when_user_not_found() throws Exception {
        when(userRepository.findByUserId("brad")).thenReturn(Optional.empty());

        userService.login("brad", "1234");
    }

    @Test(expected = UnAuthenticationException.class)
    public void login_failed_when_mismatch_password() throws Exception {
        when(userRepository.findByUserId(BRAD.getUserId())).thenReturn(Optional.of(BRAD));

        userService.login(BRAD.getUserId(), BRAD.getPassword() + "2");
    }
}
