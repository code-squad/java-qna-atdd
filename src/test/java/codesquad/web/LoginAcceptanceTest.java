package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.builder.PostRequestBuilder;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void loginForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void login() throws Exception {
        String userId = "javajigi";
        String password = "test";
        PostRequestBuilder postBuilder = PostRequestBuilder.urlEncodedHeader();
        postBuilder.addParam("userId", userId);
        postBuilder.addParam("password", password);

        ResponseEntity<String> response = template().postForEntity("/login", postBuilder.build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        log.debug("body: {}", response.getBody());
        assertNotNull(userRepository.findByUserId(userId));
        assertThat(response.getHeaders().getLocation().getPath(), startsWith("/users"));

    }

    @Test
    public void login_패스워드_틀림() {
        String userId = "javajigi";
        String password = "password";
        PostRequestBuilder postBuilder = PostRequestBuilder.urlEncodedHeader();
        postBuilder.addParam("userId", userId);
        postBuilder.addParam("password", password);

        ResponseEntity<String> response = template().postForEntity("/login", postBuilder.build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body: {}", response.getBody());
        // login_fail template check
        assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요."), is(true));

    }

    @Test
    public void logout(){
        PostRequestBuilder postBuilder = PostRequestBuilder.urlEncodedHeader();
        ResponseEntity<String> response = template().postForEntity("/logout", postBuilder.build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        log.debug("header: {}", response.getHeaders());
        log.debug("body: {}", response.getBody());
    }
}
