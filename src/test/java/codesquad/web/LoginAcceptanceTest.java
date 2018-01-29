package codesquad.web;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static codesquad.utils.HtmlFormDataBuilder.urlEncodedForm;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {

    private ResponseEntity<String> loginUser(String userId, String password) {
        HttpEntity<MultiValueMap<String, Object>> request = urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", password)
                .build();
        return template().postForEntity("/login", request, String.class);
    }

    @Test
    public void 로그인_성공() {
        ResponseEntity<String> response = loginUser("javajigi", "test");
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void 로그인_잘못된_아이디() {
        ResponseEntity<String> response = loginUser("javajigi2", "test");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다."), is(true));
    }

    @Test
    public void 로그인_잘못된_패스워드() {
        ResponseEntity<String> response = loginUser("javajigi", "test2");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다."), is(true));
    }
}
