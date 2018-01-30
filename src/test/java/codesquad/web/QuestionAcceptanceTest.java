package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() {
        ResponseEntity<String> response = createQna(basicAuthTemplate(defaultUser()));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(questionRepository.findAll().size(), is(3));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/"));
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .getForEntity(String.format("/questions/%d/form", defaultQuestion().getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertTrue(response.getBody().contains(defaultQuestion().getContents()));
    }

    @Test
    public void update() {
        ResponseEntity<String> response = updateQna(basicAuthTemplate(defaultUser()));
        Question question = questionRepository.findOne(defaultQuestion().getId());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(question, not(defaultQuestion()));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/"));
    }

    @Test
    public void show() {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .getForEntity(String.format("/questions/%d", defaultQuestion().getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertTrue(response.getBody().contains(defaultQuestion().getContents()));
    }

    @Test
    public void delete() {
        ResponseEntity<String> response = deleteQna(basicAuthTemplate(findByUserId("sanjigi")));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/"));
        assertTrue(questionRepository.findOne(2L).isDeleted());
    }

    private ResponseEntity<String> createQna(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "title")
                .addParameter("contents", "contents")
                .build();

        return template.postForEntity("/questions", request, String.class);
    }

    private ResponseEntity<String> updateQna(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("id", String.valueOf(defaultQuestion().getId()))
                .addParameter("title", "updateTitle")
                .addParameter("contents", "updateContents")
                .build();

        return template.postForEntity("/questions", request, String.class);
    }

    private ResponseEntity<String> deleteQna(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .addParameter("id", String.valueOf(2L))
                .build();

        return template.postForEntity(String.format("/questions/%d", 2L), request, String.class);
    }
}
