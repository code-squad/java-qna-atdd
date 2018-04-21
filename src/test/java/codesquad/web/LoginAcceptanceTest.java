package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void 로그인성공() throws Exception {
        String userId = "testuser";
        ResponseEntity<String> response =
                template().postForEntity(
                        "/users/login",
                        HtmlFormDataBuilder.urlEncodedForm()
                                .addParameter("userId", "javajigi")
                                .addParameter("password", "test")
                                .build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertNotNull(userRepository.findByUserId(userId));
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
    }

    @Test
    public void 로그인실패() throws Exception {
        String userId = "testuser";
        ResponseEntity<String> response =
                template().postForEntity(
                        "/users/login",
                        HtmlFormDataBuilder.urlEncodedForm()
                                .addParameter("userId", "javajigi")
                                .addParameter("password", "test1").build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

}
