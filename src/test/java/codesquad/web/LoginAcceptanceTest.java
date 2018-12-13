package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.slf4j.LoggerFactory.getLogger;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(LoginAcceptanceTest.class);

    @Test
    public void loginForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/login/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void login() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodingForm()
                .addParameter("userId", "javajigi")
                .addParameter("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void login_failed() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodingForm()
                .addParameter("userId", "javajigi")
                .addParameter("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다")).isTrue();
    }
}
