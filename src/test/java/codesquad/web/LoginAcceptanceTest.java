package codesquad.web;

import codesquad.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Test
    public void loginForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void login() throws Exception {
        HtmlFormDataBuilder htmlbuilder = HtmlFormDataBuilder.urlEncodedForm();

        htmlbuilder.addParameter("userId", "javajigi");
        htmlbuilder.addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = htmlbuilder.build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void loginFail() throws Exception {
        HtmlFormDataBuilder htmlbuilder = HtmlFormDataBuilder.urlEncodedForm();

        htmlbuilder.addParameter("userId", "javajigi");
        htmlbuilder.addParameter("password", "test2");
        HttpEntity<MultiValueMap<String, Object>> request = htmlbuilder.build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다.")).isTrue();
    }
}
