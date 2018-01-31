package codesquad.web;

import codesquad.domain.UserRepository;
import codesquad.etc.HtmlFormDataBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void loginSuccessTest() throws Exception {
        HtmlFormDataBuilder headersBuilder = HtmlFormDataBuilder.urlEncodedForm();

        String userId = "javajigi";
        headersBuilder.addParameter("userId", userId)
                .addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = headersBuilder.build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
    }

    @Test
    public void loginFailTest_invalid_pw() throws Exception {
        HtmlFormDataBuilder headersBuilder = HtmlFormDataBuilder.urlEncodedForm();

        String userId = "javajigi";
        headersBuilder.addParameter("userId", userId)
                .addParameter("password", "test1");
        HttpEntity<MultiValueMap<String, Object>> request = headersBuilder.build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void loginFailTest_invalid_id() throws Exception {
        HtmlFormDataBuilder headersBuilder = HtmlFormDataBuilder.urlEncodedForm();

        String userId = "javajigi1";
        headersBuilder.addParameter("userId", userId)
                .addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = headersBuilder.build();

        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}
