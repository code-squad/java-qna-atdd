package codesquad.web;

import codesquad.util.HtmlFormDataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    private HtmlFormDataBuilder builder;
    private String userId;

    @Before
    public void setUp() {
        builder = HtmlFormDataBuilder.urlEncodedForm();
        userId = "learner";
    }

    @Test
    public void login_form() {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void login_success() {
        builder.addParameter("userId", userId);
        builder.addParameter("password", "9229");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void login_failed() {
        builder.addParameter("userId", userId);
        builder.addParameter("password", "1234");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
