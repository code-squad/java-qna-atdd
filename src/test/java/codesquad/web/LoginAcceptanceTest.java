package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.html.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author sangsik.kim
 */
public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Test
    public void login_form() {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void login() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("userId", "javajigi")
                .addParameter("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        log.debug("body : {}", response.getBody());
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
    }

    @Test
    public void login_fail() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("userId", "javajigi")
                .addParameter("password", "invalidPassword")
                .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }
}
