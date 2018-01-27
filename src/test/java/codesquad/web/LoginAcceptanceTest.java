package codesquad.web;

import codesquad.util.HtmlFormDataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class LoginAcceptanceTest extends AcceptanceTest{
    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void setUp() {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void loginForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/login/form",String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void login() throws Exception{
        htmlFormDataBuilder.addParameter("userId","javajigi");
        htmlFormDataBuilder.addParameter("password","test");

        HttpEntity<MultiValueMap<String,Object>> request = htmlFormDataBuilder.build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void login_fail() {
        htmlFormDataBuilder.addParameter("userId","javajigi");
        htmlFormDataBuilder.addParameter("password","1234");

        HttpEntity<MultiValueMap<String,Object>> request = htmlFormDataBuilder.build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요."));

    }


}
