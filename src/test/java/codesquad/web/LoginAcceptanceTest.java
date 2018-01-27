package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import codesquad.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.utils.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

/**
 * Created by Joeylee on 2018-01-27.
 */
public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private UserService userService;

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void setUp() throws Exception {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void login_success() throws Exception {
       htmlFormDataBuilder.addParameter("userId", "sanjigi")
                .addParameter("password", "test");

        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormDataBuilder.build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void login_failed() throws Exception {

        htmlFormDataBuilder.addParameter("userId", "sanjigi")
                .addParameter("password", "!!!");

        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormDataBuilder.build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertTrue(response.getBody().contains("아이디 또는 비밀번호가 틀립니다. 다시 로그인 해주세요."));

    }
}
