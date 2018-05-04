package support.test;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {
    private static final String DEFAULT_LOGIN_USER = "javajigi";

    @Autowired
    private TestRestTemplate template;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;
    
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

    protected Question findByQuestionId(long questionId) {
        return questionRepository.findOne(questionId);
    }

    protected String createResource(String path, Object bodyPayload) {
        ResponseEntity<String> response = template().postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        return response.getHeaders().getLocation().getPath();
    }

    protected ResponseEntity<String> createResource(User loginUser,String path, Object bodyPayload) {
        return basicAuthTemplate(loginUser).postForEntity(path, bodyPayload, String.class);
    }

    protected ResponseEntity<String> getResource(String location, User loginUser) {
        return basicAuthTemplate(loginUser).getForEntity(location, String.class);
    }

    protected <T> T getResource(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }
}
