package codesquad.web;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.encodeform();
        builder.addParameter("title", "test title");
        builder.addParameter("contents", "test content");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        return template.postForEntity("/questions", request, String.class);
    }

    @Test
    public void create() throws Exception {
        ResponseEntity<String> response = create(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        log.debug("question created body : {}", response.getBody());
    }

    @Test
    public void create_fail_require_login() throws Exception {
        ResponseEntity<String> response = create(template());
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    private ResponseEntity<String> sendGetRequest(TestRestTemplate template, String url) {
        return template.getForEntity(url, String.class);
    }

    @Test
    public void read() {
        ResponseEntity<String> response = sendGetRequest(template(), "/questions/1");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void read_fail_not_exist() {
        ResponseEntity<String> response =sendGetRequest(template(), "/questions/100");
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void edit() {
        ResponseEntity<String> response = sendGetRequest(basicAuthTemplate(), "/questions/1/form");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void edit_unAuthorize() {
        ResponseEntity<String> response = sendGetRequest(basicAuthTemplate(), "/questions/2/form");
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void edit_unAuthentication() {
        ResponseEntity<String> response = sendGetRequest(template(), "/questions/1/form");
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void edit_not_exist() {
        ResponseEntity<String> response = sendGetRequest(basicAuthTemplate(), "/questions/100/form");
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.encodeform();
        builder.addParameter("_method", "put");
        builder.addParameter("title", "modify title");
        builder.addParameter("content", "modify content");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        return template.postForEntity("/questions/1", request, String.class);
    }

    @Test
    public void update() {
        ResponseEntity<String> response = update(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void update_fail_require_login() {
        ResponseEntity<String> response = update(template());
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void update_fail_not_match_writer() {
        ResponseEntity<String> response = update(basicAuthTemplate(findByUserId("sanjigi")));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> delete(TestRestTemplate template) {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.encodeform();
        builder.addParameter("_method", "delete");
        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        return template.postForEntity("/questions/1", request, String.class);
    }

    @Test
    public void delete_fail_require_login() {
        ResponseEntity<String> response = delete(template());
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void delete_fail_not_owner() {
        ResponseEntity<String> response = delete(basicAuthTemplate(findByUserId("sanjigi")));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_fail_not_answer_owner() {
        ResponseEntity<String> response = delete(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }
}
