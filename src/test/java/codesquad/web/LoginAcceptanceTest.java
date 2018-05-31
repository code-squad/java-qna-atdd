package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.security.HttpSessionUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

import support.test.AcceptanceTest;

import javax.servlet.http.HttpSession;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class LoginAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Test
    public void login() throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        builder.addParameter("userId", "riverway");
        builder.addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/users"));
    }

    @Test
    public void login_fail() throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        builder.addParameter("userId", "riverway");
        builder.addParameter("password", "failTest");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
