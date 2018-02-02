package codesquad.web;

import codesquad.domain.*;
import codesquad.util.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    private static final String TITLE_NO1 = "국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?";

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createFormFailedBecauseOfNoLogin() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() {
        HttpEntity<MultiValueMap<String, Object>> request = makeQuestionRequest("title test", "contents test");
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
    }

    @Test
    public void createFailedBecauseOfNoLogin() {
        HttpEntity<MultiValueMap<String, Object>> request = makeQuestionRequest("title test", "contents test");
        ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private HttpEntity<MultiValueMap<String, Object>> makeQuestionRequest(String title, String contents) {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder.addParameter("title", title);
        htmlFormDataBuilder.addParameter("contents", contents);
        return htmlFormDataBuilder.build();
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(TITLE_NO1), is(true));
        assertThat(response.getBody().contains("runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?"), is(true));
    }

    @Test
    public void getOne() {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(TITLE_NO1), is(true));
    }

    @Test
    public void updateFormFailedBecauseOfNoLogin() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 1), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity(String.format("/questions/%d/form", 1), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(TITLE_NO1), is(true));
    }

    @Test
    public void update() {
        HttpEntity<MultiValueMap<String, Object>> request = makeQuestionRequest("title test", "contents test");
        basicAuthTemplate(defaultUserAsSANJIGI()).put("/questions/4", request, String.class);

        Question question = questionRepository.findOne(4L);
        assertEquals("title test", question.getTitle());
    }

    @Test
    public void updateFailedBecauseOfAnotherUser() {
        HttpEntity<MultiValueMap<String, Object>> request = makeQuestionRequest("title test", "contents test");
        basicAuthTemplate().put("/questions/2", request, String.class);

        Question question = questionRepository.findOne(2L);
        assertNotEquals("title test", question.getTitle());
    }

    @Test
    public void delete() {
        basicAuthTemplate(defaultUserAsSANJIGI()).delete("/questions/3");

        Question question = questionRepository.findOne(3L);
        assertTrue(question.isDeleted());

        ResponseEntity<String> response = template().getForEntity("/questions/3", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains("expected delete"), is(false));
    }

    @Test
    public void deleteFailedBecauseOfAnotherUser() {
        basicAuthTemplate().delete("/questions/4");

        Question question = questionRepository.findOne(4L);
        assertFalse(question.isDeleted());
    }

}
