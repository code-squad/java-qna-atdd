package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    private HtmlFormDataBuilder htmlFormDataBuilde;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void loginForm() {
        ResponseEntity<String> response = template().getForEntity("/login/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.debug("body : {}", response.getBody());
    }

    @Test
    public void login() {
        htmlFormDataBuilde = HtmlFormDataBuilder.urlEncodedForm();

        htmlFormDataBuilde.addParameter("userId", "javajigi");
        htmlFormDataBuilde.addParameter("password", "test");

        ResponseEntity<String> response = template().postForEntity("/login", htmlFormDataBuilde.build(), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void login_failed() {
        htmlFormDataBuilde = HtmlFormDataBuilder.urlEncodedForm();

        htmlFormDataBuilde.addParameter("userId", "javajigi");
        htmlFormDataBuilde.addParameter("password", "test2");

        ResponseEntity<String> response = template().postForEntity("/login", htmlFormDataBuilde.build(), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다.")).isTrue();
    }
}
