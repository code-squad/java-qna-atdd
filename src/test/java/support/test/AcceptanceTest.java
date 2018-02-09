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
    private static final String API_PATH = "/api/";
    private static final String DEFAULT_LOGIN_USER = "javajigi";
    private static final String DIFFERENT_LOGIN_USER = "sanjigi";


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

    protected User differentUser() {
        return findByUserId(DIFFERENT_LOGIN_USER);
    }

    
    protected User findByUserId(String userId) {
        return userRepository.findByUserId(userId).get();
    }

    protected <T> ResponseEntity<String> createResource(User loginUser, Object payload, Class<T> clazz) {
        TestRestTemplate testRestTemplate = loginUser == null ? template() : basicAuthTemplate(loginUser);
        ResponseEntity<String> response = testRestTemplate.postForEntity(getApiPath(clazz), payload, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        return response;
    }

    protected <T> T getResource(User loginUser, String location, Class<T> responseType) {
        return basicAuthTemplate(loginUser).getForObject(location, responseType);
    }

    protected <T> String getApiPath(Class<T> clazz) {
        if (clazz == null) return "";
        return clazz instanceof Class ? API_PATH + clazz.getSimpleName().toLowerCase()+"s" : "";
    }
}
