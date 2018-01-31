package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {

    @Test
    public void login() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void login_성공() throws Exception {
        User javajigi = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");

        ResponseEntity<String> response = template().postForEntity("/login", htmlRequest(javajigi), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void login_패스워드가다른경우() throws Exception {
        User javajigi = new User(1, "javajigi", "wrong", "자바지기", "javajigi@slipp.net");

        ResponseEntity<String> response = template().postForEntity("/login", htmlRequest(javajigi), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void login_아이디패스워드가없는경우() throws Exception {
        User test = new User(1, null, null, "자바지기", "javajigi@slipp.net");

        ResponseEntity<String> response = template().postForEntity("/login", htmlRequest(test), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void login_존재하지않는회원() throws Exception {
        User gunju = new User(1, "gunju", "test", "자바지기", "javajigi@slipp.net");

        ResponseEntity<String> response = template().postForEntity("/login", htmlRequest(gunju), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void logout() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/logout", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

}
