package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LoginAcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private TestRestTemplate template;

    private HtmlFormDataBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = HtmlFormDataBuilder.encodeform();
    }

    @Test
    public void login() {
        String userId = "javajigi";
        builder.addParameter("userId", userId);
        builder.addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        ResponseEntity<String> response = template.postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
        assertNotNull(userRepo.findByUserId(userId));
        log.debug(response.getBody());
    }

    @Test
    public void login_fail_invalid_password() {
        String userId = "javajigi";
        builder.addParameter("userId", userId);
        builder.addParameter("password", "1111");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        ResponseEntity<String> response = template.postForEntity("/users/login", request, String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void login_fail_invalid_userId() {
        String userId = "colin";
        builder.addParameter("userId", userId);
        builder.addParameter("password", "1234");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        ResponseEntity<String> response = template.postForEntity("/users/login", request, String.class);

        assertFalse(userRepo.findByUserId(userId).isPresent());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
