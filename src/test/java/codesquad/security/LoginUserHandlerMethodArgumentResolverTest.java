package codesquad.security;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import support.test.BaseTest;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginUserHandlerMethodArgumentResolverTest extends BaseTest {
    @Mock
    private MethodParameter parameter;

    @Mock
    private NativeWebRequest request;

    @Mock
    private LoginUser annotedLoginUser;

    private LoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

    @Before
    public void setup() {
        loginUserHandlerMethodArgumentResolver = new LoginUserHandlerMethodArgumentResolver();
    }

    @Test
    public void loginUser_normal() throws Exception {
        User sessionUser = new User("sanjigi", "password", "name", "javajigi@slipp.net");
        when(request.getAttribute(HttpSessionUtils.USER_SESSION_KEY, WebRequest.SCOPE_SESSION)).thenReturn(sessionUser);

        User loginUser = (User) loginUserHandlerMethodArgumentResolver.resolveArgument(parameter, null, request, null);

        softly.assertThat(loginUser).isEqualTo(sessionUser);
    }

    @Test(expected = UnAuthenticationException.class)
    public void loginUser_required_guest() throws Exception {
        when(annotedLoginUser.required()).thenReturn(true);
        when(parameter.getParameterAnnotation(LoginUser.class)).thenReturn(annotedLoginUser);
        when(request.getAttribute(HttpSessionUtils.USER_SESSION_KEY, WebRequest.SCOPE_SESSION))
                .thenReturn(User.GUEST_USER);

        loginUserHandlerMethodArgumentResolver.resolveArgument(parameter, null, request, null);
    }

    @Test
    public void loginUser_not_required_guest() throws Exception {
        when(annotedLoginUser.required()).thenReturn(false);
        when(parameter.getParameterAnnotation(LoginUser.class)).thenReturn(annotedLoginUser);
        when(request.getAttribute(HttpSessionUtils.USER_SESSION_KEY, WebRequest.SCOPE_SESSION))
                .thenReturn(User.GUEST_USER);

        User guestUser = (User) loginUserHandlerMethodArgumentResolver.resolveArgument(parameter, null, request, null);
        softly.assertThat(guestUser).isEqualTo(User.GUEST_USER);
    }

    @Test
    public void supportsParameter_false() {
        when(parameter.hasParameterAnnotation(LoginUser.class)).thenReturn(false);

        softly.assertThat(loginUserHandlerMethodArgumentResolver.supportsParameter(parameter)).isFalse();
    }

    @Test
    public void supportsParameter_true() {
        when(parameter.hasParameterAnnotation(LoginUser.class)).thenReturn(true);

        softly.assertThat(loginUserHandlerMethodArgumentResolver.supportsParameter(parameter)).isTrue();
    }
}
