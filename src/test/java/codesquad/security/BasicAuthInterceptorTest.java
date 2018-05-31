package codesquad.security;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import codesquad.domain.User;
import codesquad.security.BasicAuthInterceptor;
import codesquad.security.HttpSessionUtils;
import codesquad.service.UserService;

/*
* DB에 의존하는 테스트를 Mock을 이용하여 가짜로 테스트 하기 때문에 서버를 켜지 않고 금방 끝남
* */

@RunWith(MockitoJUnitRunner.class)
public class BasicAuthInterceptorTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private BasicAuthInterceptor basicAuthInterceptor;

    @Test
    public void preHandle_로그인_성공() throws Exception {
        String userId = "userId";
        String password = "password";
        MockHttpServletRequest request = basicAuthHttpRequest(userId, password);
        User loginUser = new User(userId, password, "name", "javajigi@slipp.net");

        // 해당 기능을 만나게 되면, thenReturn 값으로 리턴하라는 뜻. 이때 login method에 의존하고 있는 userService를 Mock으로 설정.
        when(userService.login(userId, password)).thenReturn(loginUser);

        basicAuthInterceptor.preHandle(request, null, null);
        assertThat(request.getSession().getAttribute(HttpSessionUtils.USER_SESSION_KEY), is(loginUser));
    }

    private MockHttpServletRequest basicAuthHttpRequest(String userId, String password) {
        String encodedBasicAuth = Base64.getEncoder()
                .encodeToString(String.format("%s:%s", userId, password).getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic " + encodedBasicAuth);
        return request;
    }
}
