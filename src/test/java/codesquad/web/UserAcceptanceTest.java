package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import support.test.AcceptanceTest;

// 인수 테스트는 사용자 입장 기능에 대한 테스트 따라서, 단위 테스트와는 다르게 exception에 관한 테스트가 없다. 왜냐하면 예외에 관한 응답을 주기 때문이다.
public class UserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);
    private HttpEntity<MultiValueMap<String, Object>> request;
    private ResponseEntity<String> response;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createForm() throws Exception {
        response = template().getForEntity("/users/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {

        String userId = "testuser";

        request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", userId)
                .addParameter("password", "password")
                .addParameter("name", "자바지기")
                .addParameter("email", "javajigi@slipp.net").build();
        log.debug("request : {}", request);
        response = template().postForEntity("/users", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertNotNull(userRepository.findByUserId(userId));
        log.debug("response headers : ", response.getHeaders());
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
    }

    @Test
    public void list() throws Exception {
        response = template().getForEntity("/users", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(defaultUser().getEmail()), is(true));
    }

    @Test
    public void updateForm_no_login() throws Exception {
        response = template().getForEntity(String.format("/users/%d/form", defaultUser().getId()),
                String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm_login() throws Exception {
        User loginUser = defaultUser();
        response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/users/%d/form", loginUser.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(loginUser.getEmail()), is(true));
    }

    @Test
    public void update_no_login() throws Exception {
        response = update(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {

        request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("password", "password2")
                .addParameter("name", "자바지기2")
                .addParameter("email", "javajigi@slipp.net").build();

        return template.postForEntity(String.format("/users/%d", defaultUser().getId()), request, String.class);
    }

    @Test
    public void update() throws Exception {
        response = update(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/users"));
    }
}
