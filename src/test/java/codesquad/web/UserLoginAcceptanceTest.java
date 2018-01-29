package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


public class UserLoginAcceptanceTest extends AcceptanceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void login_success_then_redirect_users() throws Exception {
        String userId = "javajigi";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertNotNull(userRepository.findByUserId(userId));
        String path = response.getHeaders().getLocation().getPath();
        assertThat(path, startsWith("/users"));
    }

    @Test
    public void login_fail_then_redirect_users() throws Exception {
        String userId = "testuser";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", "password")
                .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(userRepository.findByUserId(userId));
        assertThat(response.getBody(), containsString("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요."));
    }
}
