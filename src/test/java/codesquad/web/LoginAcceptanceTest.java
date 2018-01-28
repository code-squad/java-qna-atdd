package codesquad.web;

import codesquad.UnAuthenticationException;
import codesquad.domain.User;
import codesquad.helper.HtmlFormDataBuilder;
import codesquad.service.UserService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import support.test.AcceptanceTest;

public class LoginAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private UserService userService;

    @Test
    public void login_성공(){

        HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("userId", "javajigi")
                .addParameter("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
    }

    @Test(expected = UnAuthenticationException.class)
    public void login_실패() throws UnAuthenticationException {
        User loginUser = defaultUser();

        HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("userId", loginUser.getUserId())
                .addParameter("password", "실패")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());

        userService.login(loginUser.getUserId(), "실패");
    }
}
