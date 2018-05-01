package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import codesquad.domain.QuestionRepository;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        final ResponseEntity<String> response = create(basicAuthTemplate());

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/home"));
    }

    @Test
    public void create_no_login() throws Exception {
        final ResponseEntity<String> response = create(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        String title = "질문제목_질문생성";
        final HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", "질문내용_질문생성")
                .build();

        return template.postForEntity("/questions", request, String.class);
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(defaultQuestion().getTitle()), is(true));
        assertThat(response.getBody().contains(defaultQuestion().getWriter().getName()), is(true));
        assertThat(response.getBody().contains(defaultQuestion().getFormattedCreateDate()), is(true));
        assertThat(response.getBody().contains(defaultQuestion().getContents()), is(false));
    }

    @Test
    public void read() {
        final Question question = defaultQuestion();
        final ResponseEntity<String> response = template().getForEntity(question.generateUrl(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(question.generateUrl() + "/form"), is(true));
        assertThat(response.getBody().contains(question.getTitle()), is(true));
        assertThat(response.getBody().contains(question.getContents()), is(true));
        assertThat(response.getBody().contains(question.getFormattedCreateDate()), is(true));
    }

    @Test
    public void updateForm_no_login() throws Exception {
        final ResponseEntity<String> response = template().getForEntity(defaultQuestion().generateUrl() + "/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm() throws Exception {
        final ResponseEntity<String> response = basicAuthTemplate().getForEntity(defaultQuestion().generateUrl() + "/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void updateForm_other_user() throws Exception {
        User otherUser = new User(10, "otherUser", "pw11", "name", "email");
        final ResponseEntity<String> response = basicAuthTemplate(otherUser).getForEntity(defaultQuestion().generateUrl() + "/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_no_login() throws Exception {
        final ResponseEntity<String> response = update(template());

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() throws Exception {
        final ResponseEntity<String> response = update(basicAuthTemplate());

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/home"));
    }

    @Test
    public void update_other_user() {
        User otherUser = new User(10, "otherUser", "pw11", "name", "email");
        final ResponseEntity<String> response = update(basicAuthTemplate(otherUser));

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        final HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "수정된 질문 제목")
                .addParameter("contents", "수정된 질문 내용")
                .build();

        return template.postForEntity(defaultQuestion().generateUrl(), request, String.class);
    }

    @Test
    public void delete_no_login() {
        ResponseEntity<String> response = delete(template(), defaultQuestion());

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_other_user() {
        User otherUser = new User(10, "otherUser", "pw11", "name", "email");
        ResponseEntity<String> response = delete(basicAuthTemplate(otherUser), defaultQuestion());

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_owner() {
        final User sanjigi = findByUserId("sanjigi");
        final Question questionBySanjigi = findByQuestionId(2);
        ResponseEntity<String> response = delete(basicAuthTemplate(sanjigi), questionBySanjigi);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/home"));
        assertTrue(questionRepository.findOne(findByQuestionId(2).getId()).isDeleted());
    }

    private ResponseEntity<String> delete(TestRestTemplate template, Question question) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();
        return template.postForEntity(question.generateUrl(), request, String.class);
    }
}