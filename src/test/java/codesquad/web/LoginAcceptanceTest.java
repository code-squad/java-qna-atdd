package codesquad.web;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class LoginAcceptanceTest extends AcceptanceTest {

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void setUp() {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("userId", "javajigi");
    }

    @Test
    public void login() {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("password", "test")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
    }

    @Test
    public void login_fail() {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("password", "wrongPassword")
                .build();

        ResponseEntity<String> response = template().postForEntity("/users/login", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/user/login_failed"));
    }
}
