package support.test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import codesquad.domain.User;
import codesquad.domain.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;
import static testhelper.HtmlFormDataBuilder.toJson;
import static testhelper.HtmlFormDataBuilder.urlEncodedForm;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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

    public ResponseEntity<String> delete(String url) {
        return basicAuthTemplate()
                .exchange(url, HttpMethod.DELETE, urlEncodedForm().build(), String.class);
    }

    public ResponseEntity<String> put(String url, HttpEntity<?> request) {
        return basicAuthTemplate()
                .exchange(url, HttpMethod.PUT, request, String.class);
    }

    public ResponseEntity<String> put(String url, Object bodyPayload) {
        return basicAuthTemplate()
                .exchange(url, HttpMethod.PUT, toJson(bodyPayload), String.class);
    }

    public String create(String url, Object bodyPayload) {
        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity(url, bodyPayload, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        return response.getHeaders().getLocation().getPath();
    }

    protected User defaultUser() {
        return findByUserId(DEFAULT_LOGIN_USER);
    }
    
    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }
}
