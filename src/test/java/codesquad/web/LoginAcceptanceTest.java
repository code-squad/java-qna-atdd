package codesquad.web;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.UserDto;
import codesquad.web.utils.HtmlFormDataBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @Before
    public void setup() {
        testUser = createUserDto("testUser", "password").toUser();
        userRepository.save(testUser);
    }

    @After
    public void tearDown() {
        userRepository.delete(testUser.getId());
    }

    private UserDto createUserDto(String userId, String password) {
        return new UserDto(userId, password, "testUser", "user@test.com");
    }

    private ResponseEntity<String> loginUser(String userId, String password) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", password)
                .build();
        return template().postForEntity("/login", request, String.class);
    }

    @Test
    public void loginForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void login() throws Exception {
        ResponseEntity<String> response = loginUser(testUser.getUserId(), testUser.getPassword());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        String redirectPath = response.getHeaders().getLocation().getPath();
        assertThat(redirectPath, is("/users"));
    }

    @Test
    public void login_잘못된_패스워드() throws Exception {
        ResponseEntity<String> response = loginUser(testUser.getUserId(), testUser.getPassword() + "1");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다."), is(true));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void login_잘못된_아이디() throws Exception {
        ResponseEntity<String> response = loginUser(testUser.getUserId() + "1", testUser.getPassword());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains("아이디 또는 비밀번호가 틀립니다."), is(true));
        log.debug("body : {}", response.getBody());
    }
}