package codesquad.web;

import codesquad.converter.HtmlFormDataBuilder;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

public class LoginAcceptanceTest extends AcceptanceTest {
    @Test
    public void loginTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HtmlFormDataBuilder htmlFormDataBuilder = new HtmlFormDataBuilder(headers)
                .addParams("userId", "javajigi")
                .addParams("password", "test")
                .addParams("name", "자바지기")
                .addParams("email", "javajigi@slipp.net");

        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormDataBuilder.build() , String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/users"));
    }

    @Test
    public void loginFailTest() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        HtmlFormDataBuilder htmlFormDataBuilder = new HtmlFormDataBuilder(headers)
                .addParams("userId", "jimmy")
                .addParams("password", "test")
                .addParams("name", "자바지기")
                .addParams("email", "javajigi@slipp.net");
        ResponseEntity<String> response = template().postForEntity("/users/login", htmlFormDataBuilder.build() , String.class);
        assertEquals(response.getStatusCode().value(), 401);
    }
}
