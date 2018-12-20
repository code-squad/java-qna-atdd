package support.test;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest extends BaseTest {
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

    public ResponseEntity<Void> postResource(TestRestTemplate testRestTemplate, String location, Object obj) {
        return testRestTemplate.postForEntity(location, obj, Void.class);
    }

    public HttpStatus getStatusCode(ResponseEntity responseEntity) {
        return responseEntity.getStatusCode();
    }

    public String getLocation(ResponseEntity<Void> responseEntity) {
        return responseEntity.getHeaders().getLocation().getPath();
    }

    public ResponseEntity exchangeResource(TestRestTemplate testRestTemplate, String location, HttpMethod httpMethod, Object obj) {
        return testRestTemplate.exchange(URI.create(location),httpMethod, new HttpEntity(obj, new HttpHeaders()), Void.class);
    }
}
