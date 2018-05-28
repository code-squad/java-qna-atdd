package codesquad.web;

import codesquad.domain.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginAcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void loginTest() throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        String userId = "sanjigi";
        String password = "test";
        builder.addParams("userId", userId);
        builder.addParams("password", password);
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        ResponseEntity<String> response = template.postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(userRepository.findByUserId(userId).isPresent(), is(true));
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
    }
}
