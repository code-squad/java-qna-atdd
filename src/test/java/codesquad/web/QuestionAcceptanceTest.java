package codesquad.web;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void show() {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void write_form() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void write() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions",
                HtmlFormDataBuilder.createRequest(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void update() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange(String.format("/questions/%d", 1), HttpMethod.PUT, HtmlFormDataBuilder.updateRequest(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }


    @Test
    public void update_unauthorized() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange(String.format("/questions/%d", 2), HttpMethod.PUT, HtmlFormDataBuilder.updateRequest(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(String.format("/questions/%d/form", 1), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void updateForm_exception() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(String.format("/questions/%d/form", 2), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity(String.format("/questions/%d", 1),
                HtmlFormDataBuilder.deleteRequest(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }


}
