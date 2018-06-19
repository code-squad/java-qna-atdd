package codesquad.web;

import codesquad.HtmlFormDataBuilder;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.service.QnaService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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

    @Test
    public void update_no_login() {
        QuestionDto newQuestion = new QuestionDto("test title", "test content");
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/api/qna", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto updateQuestion = new QuestionDto("hey", "wow");
        template().put(location, updateQuestion);

        QuestionDto dbQuestion = basicAuthTemplate().getForObject(location, QuestionDto.class);
        assertFalse(dbQuestion.equalsTitleAndContent(updateQuestion));
    }

    @Test
    public void update_other_user() {
        QuestionDto newQuestion = new QuestionDto("test title", "test content");
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/api/qna", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto updateQuestion = new QuestionDto("hey", "wow");
        basicAuthTemplate(new User("test", "test", "test", "test"))
                .put(location, updateQuestion);

        QuestionDto dbQuestion = basicAuthTemplate().getForObject(location, QuestionDto.class);
        assertFalse(dbQuestion.equalsTitleAndContent(updateQuestion));
    }

    @Test
    public void update() {
        QuestionDto newQuestion = new QuestionDto("test title", "test content");
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/api/qna", newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto updateQuestion = new QuestionDto("hey", "wow");
        basicAuthTemplate().put(location, updateQuestion);

        QuestionDto dbQuestion = basicAuthTemplate().getForObject(location, QuestionDto.class);
        assertTrue(dbQuestion.equalsTitleAndContent(updateQuestion));
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