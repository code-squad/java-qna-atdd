package support.test;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    public TestRestTemplate template() {
        return template;
    }

    public TestRestTemplate basicAuthTemplate() {
        return basicAuthTemplate(defaultUser());
    }

    public TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    protected User defaultUser() {
        return findByUserId(DEFAULT_LOGIN_USER);
    }

    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected <T> String createResource(TestRestTemplate template, String path, T bodyPayload) {
        ResponseEntity<String> response = requestPost(template, path, bodyPayload);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> ResponseEntity<String> requestPost(TestRestTemplate template, String path, T bodyPayload) {
        return template.postForEntity(path, bodyPayload, String.class);
    }

    protected <T> T requestGetForRest(TestRestTemplate template, String path, Class<T> responseType) {
        return template.getForObject(path, responseType);
    }

    protected <T> ResponseEntity<T> requestGet(String path, Class<T> responseType) {
        return requestGet(template(), path, responseType);
    }

    protected <T> ResponseEntity<T> requestGet(TestRestTemplate template, String path, Class<T> responseType) {
        return template.getForEntity(path, responseType);
    }
}
