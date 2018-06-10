package codesquad.web;

import codesquad.HtmlFormDataBuilder;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.service.QnaService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QnaService qnaService;

    @Test
    public void createForm() {
        ResponseEntity<String> responseEntity = template().getForEntity("/qna/form", String.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        log.debug("body {}", responseEntity.getBody());
    }

    @Test
    public void create() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "title Test")
                .addParameter("contents", "contents Test")
                .build();
        ResponseEntity<String> responseEntity = basicAuthTemplate().postForEntity("/qna", request, String.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void list() {
        Iterable<Question> questions = qnaService.findAll();
        ResponseEntity<String> responseEntity = template().getForEntity("/", String.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void read() {
        ResponseEntity<String> responseEntity = template().getForEntity("/qna/" + 1, String.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));

        // TODO 테스트 문제
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity(String.format("/qna/%d/form", 1), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm_login() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/qna/%d/form", 1), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    // TODO testRestTemplate
    private ResponseEntity<String> updateAsist(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "testtitle")
                .addParameter("contents", "testcontents")
                .build();

        return template.exchange(String.format("/qna/%d", defaultUser().getId()), HttpMethod.PUT, request, String.class);
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = updateAsist(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_other_user() {
        User testUser = new User("newtestuser", "newtestpass", "newtestname", "newtest@email.com");
        ResponseEntity<String> response = updateAsist(basicAuthTemplate(testUser));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() {
        ResponseEntity<String> response = updateAsist(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(Objects.requireNonNull(response.getHeaders().getLocation()).getPath().startsWith("/qna"));
    }

    @Test
    public void delete_no_login() {
        Long questionCount = qnaService.questionCount();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<String> response = template().exchange(String.format("/qna/%d", questionCount),
                HttpMethod.DELETE, entity, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_other_user() {
        User testUser = new User("newtestuser", "newtestpass", "newtestname", "newtest@email.com");
        Long questionCount = qnaService.questionCount();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<String> response = basicAuthTemplate(testUser).exchange(String.format("/qna/%d", questionCount),
                HttpMethod.DELETE, entity, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete() {
        Long questionCount = qnaService.questionCount();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<String> response = basicAuthTemplate().exchange(String.format("/qna/%d", questionCount),
                HttpMethod.DELETE, entity, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }
}