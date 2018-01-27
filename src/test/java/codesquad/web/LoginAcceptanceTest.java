package codesquad.web;

import codesquad.domain.UserRepository;
import codesquad.util.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void loginSuccess() {
        String userId = "sanjigi";
        String password = "test";
        assertNotNull(userRepository.findByUserId(userId));

        ResponseEntity<String> response = tryLogin(userId, password);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        // ex) Actual   :/users;jsessionid=C019951E8418106F75BCDAEC35E5BDA4
        assertThat(response.getHeaders().getLocation().getPath(), startsWith("/users"));
    }

    @Test
    public void loginFailedWhenMismatchPassword() {
        String userId = "sanjigi";
        String password = "testMISMATCH";
        assertNotNull(userRepository.findByUserId(userId));

        ResponseEntity<String> response = tryLogin(userId, password);
        testFailedMessage(response);
    }

    private void testFailedMessage(ResponseEntity<String> response) {
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), containsString("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요."));
    }

    @Test
    public void loginFailedWhenUserNotFound() {
        String userId = "WINDjigi";
        String password = "test";
        assertNotNull(userRepository.findByUserId(userId));

        ResponseEntity<String> response = tryLogin(userId, password);
        testFailedMessage(response);
    }

    private ResponseEntity<String> tryLogin(String userId, String password) {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();

        htmlFormDataBuilder.addParameter("userId", userId);
        htmlFormDataBuilder.addParameter("password", password);

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        return template().postForEntity("/login", request, String.class);
    }

    @Test
    public void logoutSuccess() {
        ResponseEntity<String> response = template().getForEntity("/logout", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
