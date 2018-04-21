package codesquad.web;

import codesquad.builder.HtmlFormDataBuilder;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LoginAcceptanceTest extends AcceptanceTest {
    @Test
    public void login() {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("userId", "javajigi");
        htmlFormDataBuilder.addParameter("password", "test");

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }
}
