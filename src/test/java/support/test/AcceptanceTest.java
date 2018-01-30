package support.test;

import codesquad.dto.QuestionDto;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import codesquad.domain.User;
import codesquad.domain.UserRepository;

import static codesquad.utils.HtmlFormDataBuilder.jsonEncodedForm;
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
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity(path, bodyPayload, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        return response.getHeaders().getLocation().getPath();
    }

    protected String putResource(String location, Object bodyPayload) {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.PUT, jsonEncodedForm().build(bodyPayload), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        return response.getHeaders().getLocation().getPath();
    }

    protected String deleteResource(String location) {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange(location, HttpMethod.DELETE, jsonEncodedForm().build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        return response.getHeaders().getLocation().getPath();
    }
}
