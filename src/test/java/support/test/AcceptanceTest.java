package support.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import codesquad.domain.User;
import codesquad.domain.UserRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";
    private static final String DEFAULT_LOGIN_USER_SAN = "sanjigi";

    @Autowired
    private TestRestTemplate template;
    
    @Autowired
    private UserRepository userRepository;
    
    public TestRestTemplate template() {
        return template;
    } 
    
    public TestRestTemplate basicAuthTemplate() {
        return basicAuthTemplate(defaultUser(DEFAULT_LOGIN_USER));
    }

    public TestRestTemplate basicAuthTemplateWithAnotherDefaultUser() {
        return basicAuthTemplate(defaultUser(DEFAULT_LOGIN_USER_SAN));
    }


    public TestRestTemplate basicAuthTemplate(User loginUser) {
        return template.withBasicAuth(loginUser.getUserId(), loginUser.getPassword());
    }

    protected User defaultUser() {
        return defaultUser(DEFAULT_LOGIN_USER);
    }

    protected User defaultUserAsSANJIGI() {
        return defaultUser(DEFAULT_LOGIN_USER_SAN);
    }

    private User defaultUser(String userId) {
        return findByUserId(userId);
    }
    
    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).orElseThrow(IllegalArgumentException::new);
    }

    protected String createResource(String path, Object bodyPayload) {
        return createResource(path, bodyPayload, defaultUser());
    }

    protected String createResource(String path, Object bodyPayload, User loginUser) {
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> T getResource(String location, Class<T> responseType) {
        return basicAuthTemplate(defaultUser()).getForObject(location, responseType);
    }

    protected <T> T getResource(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }
}
