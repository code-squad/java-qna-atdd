package codesquad.web;

import codesquad.HtmlFormDataBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Arrays;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LogManager.getLogger(LoginAcceptanceTest.class);

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }


    @Test
    public void login() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = getMultiValueMapHttpEntity("test");

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void login_faled() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = getMultiValueMapHttpEntity("asdasdas");

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요.")).isTrue();
    }

    public HttpEntity<MultiValueMap<String, Object>> getMultiValueMapHttpEntity(String password) {
        String userId = "javajigi";
        return HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", password)
                .build();
    }
}
