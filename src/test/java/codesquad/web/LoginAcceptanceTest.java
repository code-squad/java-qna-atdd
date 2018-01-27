package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class LoginAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Test
    public void loginForm() {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains("SLiPP Java Web Programming"), is(true));
    }

    @Test
    public void logout() {
        ResponseEntity<String> response = template().getForEntity("/logout", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void login() {
        ResponseEntity<String> response = template().postForEntity("/login",
                HtmlFormDataBuilder.urlEncodedForm()
                        .addParameter("userId", "javajigi").addParameter("password", "test").build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        log.debug("body : {}", response.getBody());
    }


    @Test
    public void login_wrong_password() {
        ResponseEntity<String> response = template().postForEntity("/login",
                HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", "javajigi")
                .addParameter("password", "aaa")
                .build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.info(response.getBody());
        assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요."), is(true));
    }

    @Test
    public void login_no_user() throws Exception {
        ResponseEntity<String> response = template().postForEntity("/login",
                HtmlFormDataBuilder.urlEncodedForm()
                        .addParameter("userId", "javajigi")
                        .addParameter("password", "aaa")
                        .build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.info(response.getBody());
        assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요."), is(true));
    }



}
