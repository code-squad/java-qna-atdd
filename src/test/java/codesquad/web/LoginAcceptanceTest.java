package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void 로그인_성공() {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        String userId = "javajigi";
        builder.addParameter("userId", userId);
        builder.addParameter("password", "test");

        ResponseEntity<String> response = template().postForEntity("/users/login", builder.build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/users"));
    }

    @Test
    public void 로그인_실패() {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        String userId = "javajigi2";
        builder.addParameter("userId", userId);
        builder.addParameter("password", "test");

        ResponseEntity<String> response = template().postForEntity("/users/login", builder.build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }
}
