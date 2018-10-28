package codesquad.web;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;
import support.test.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UserAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/users/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        ResponseEntity<String> response = create(template());

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertNotNull(userRepository.findByUserId("testuser"));
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
    }

    public ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        return htmlFormDataBuilderTemplate.executePostForEntity(template, "/users", Method.POST,
                (HtmlFormDataBuilder htmlFormDataBuilder) -> {

                    htmlFormDataBuilder.addParameter("userId", "testuser");
                    htmlFormDataBuilder.addParameter("password", "password");
                    htmlFormDataBuilder.addParameter("name", "자바지기");
                    htmlFormDataBuilder.addParameter("email", "javajigi@slipp.net");
                    return htmlFormDataBuilder.build();
                });
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/users", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(defaultUser().getEmail()), is(true));
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/users/%d/form", defaultUser().getId()),
                String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/users/%d/form", loginUser.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(loginUser.getEmail()), is(true));
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/users"));
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        String targetUrl = String.format("/users/%d", defaultUser().getId());
        return htmlFormDataBuilderTemplate.executePostForEntity(template, targetUrl, Method.PUT,
                (HtmlFormDataBuilder htmlFormDataBuilder) -> {

                    htmlFormDataBuilder.addParameter("password", "password2");
                    htmlFormDataBuilder.addParameter("name", "자바지기2");
                    htmlFormDataBuilder.addParameter("email", "javajigi@slipp.net");
                    return htmlFormDataBuilder.build();
                });
    }

}
