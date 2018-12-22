package support.test;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)    //서버 테스트를 실행하기 전에 실제 서버 실행
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

    protected String createResource(String path, Object bodyPayload) {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity(path, bodyPayload, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected String createUserResource(String path, Object bodyPayload) {
        ResponseEntity<Void> response = template().postForEntity(path, bodyPayload, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> T getResource(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    protected HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
