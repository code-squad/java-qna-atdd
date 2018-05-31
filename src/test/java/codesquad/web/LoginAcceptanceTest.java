package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {

    private final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    private HttpEntity<MultiValueMap<String, Object>> request;
    private ResponseEntity<String> response;

    private String userId;
    private String password;

    private HttpEntity<MultiValueMap<String, Object>> makeHttpEntity(String userId, String password) {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", password)
                .build();
    }

    @Test
    public void login_success() {

        userId = "javajigi";
        password = "test";

        request = makeHttpEntity(userId, password);

        response = template().postForEntity("/users/login", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void login_fail() {

        userId = "javajigi";
        password = "hello";

        request = makeHttpEntity(userId, password);

        response = template().postForEntity("/users/login", request, String.class);
        log.debug("response : {}", response.toString());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
