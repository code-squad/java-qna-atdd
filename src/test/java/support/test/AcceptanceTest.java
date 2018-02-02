package support.test;

import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static support.test.HtmlFormDataBuilder.urlEncodedForm;

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

    protected String createResource(String path, Object bodyPayload) {
        ResponseEntity<String> response = template().postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected String createResource(String path, Object bodyPayload, User loginUser) {
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();
    }

    protected <T> T getResource(String location, Class<T> responseType, User loginUser) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    protected <T> T getResource(String location, Class<T> responseType) {
        return template().getForObject(location, responseType);
    }

    protected void deleteResource(String path, User loginUser) {
        basicAuthTemplate(loginUser).delete(path);
    }

    protected HttpEntity<MultiValueMap<String, Object>> htmlRequest(User user) {
        return urlEncodedForm().addParameter("userId", user.getUserId())
                               .addParameter("password", user.getPassword())
                               .addParameter("name", user.getName())
                               .addParameter("email", user.getEmail())
                               .build();
    }

    protected HttpEntity<MultiValueMap<String, Object>> htmlRequest(QuestionDto question) {
        return urlEncodedForm().addParameter("title", question.getTitle())
                               .addParameter("contents", question.getContents())
                               .build();
    }

    protected HttpEntity<MultiValueMap<String, Object>> htmlRequest(AnswerDto question) {
        return urlEncodedForm().addParameter("contents", question.getContents())
                               .build();
    }
}
