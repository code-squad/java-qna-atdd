package codesquad.web;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import util.HtmlFormDataBuilder;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private TestRestTemplate template;

    @Test
    public void loginTest_Success() throws Exception {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("userId", "javajigi")
                .addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        ResponseEntity<String> response = template.postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));

    }

    @Test
    public void loginTest_Fail_InvalidPassword() throws Exception {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("userId", "javajigi")
                .addParameter("password", "asdfasdftest");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        ResponseEntity<String> response = template.postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void loginTest_Fail_InvalidId() throws Exception {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("userId", "asdfasdfasdf")
                .addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        ResponseEntity<String> response = template.postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

}
