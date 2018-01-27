package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

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
        ResponseEntity<String> response = loginTemplate(template(), "javajigi", "test");

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/users"));
    }

    @Test
    public void login_userId_실패() throws Exception {
        ResponseEntity<String> response = loginTemplate(template(), "wrongId", "test");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void login_password_실패() throws Exception {
        ResponseEntity<String> response = loginTemplate(template(), "javajigi", "wrongPassword");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    private ResponseEntity<String> loginTemplate(TestRestTemplate template, String userId, String password) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", password)
                .build();

        return template.postForEntity(String.format("/login", defaultUser().getId()), request, String.class);
    }
}
