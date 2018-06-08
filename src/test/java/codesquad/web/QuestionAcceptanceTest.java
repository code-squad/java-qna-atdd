package codesquad.web;

import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private static final String FORM_URL = "/questions/form";
    private static final String CREATE_URL = "/questions/create";
    private static final String VALID_SHOW_URL = "/questions/1";
    private static final String INVALID_SHOW_URL = "/questions/10";
    private static final String UPDATE_FORM_URL = "/questions/1/update";
    private static final String UPDATE_DELETE_URL = "/questions/1";
    private static final String DEFAULT_CONTENT = "content";
    private static final String DEFAULT_TITLE = "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?";

    @Test
    public void form_logged_in() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = createResponse(basicAuthTemplate(loginUser), FORM_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void form_NOT_logged_in() throws Exception {
        ResponseEntity<String> response = createResponse(template(), FORM_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void create_logged_in() throws Exception {
        ResponseEntity<String> response = createPutResponse(basicAuthTemplate(), CREATE_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
    }

    @Test
    public void create_NOT_logged_in() throws Exception {
        ResponseEntity<String> response = createPutResponse(template(), CREATE_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void show_Question_Exists() throws Exception {
        ResponseEntity<String> response = createResponse(template(), VALID_SHOW_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void show_Question_Does_NOT_Exist() throws Exception {
        ResponseEntity<String> response = createResponse(template(), INVALID_SHOW_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void updateForm_logged_in() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = createResponse(basicAuthTemplate(loginUser), UPDATE_FORM_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void updateForm_NOT_logged_in() throws Exception {
        ResponseEntity<String> response = createResponse(template(), UPDATE_FORM_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_logged_in() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = createPutResponse(basicAuthTemplate(loginUser), UPDATE_DELETE_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions/1"));
    }

    @Test
    public void update_NOT_logged_in() throws Exception {
        ResponseEntity<String> response = createPutResponse(template(), UPDATE_DELETE_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_logged_in() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = createDeleteResponse(basicAuthTemplate(loginUser), UPDATE_DELETE_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
    }

    @Test
    public void delete_NOT_logged_in() throws Exception {
        ResponseEntity<String> response = createDeleteResponse(template(), UPDATE_DELETE_URL);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> createResponse(TestRestTemplate template, String url) throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        return template.getForEntity(url, String.class, request);
    }

    private ResponseEntity<String> createPutResponse(TestRestTemplate template, String url) throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParams("title", DEFAULT_TITLE);
        builder.addParams("content", DEFAULT_CONTENT);
        builder.addParams("_method", "PUT");

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        return template.postForEntity(url, request, String.class);
    }

    private ResponseEntity<String> createDeleteResponse(TestRestTemplate template, String url) throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParams("_method", "DELETE");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();

        return template.postForEntity(url, request, String.class);
    }
}
