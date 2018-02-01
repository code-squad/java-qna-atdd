package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.builder.PostRequestBuilder;
import support.test.AcceptanceTest;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void list() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body: {}", response.getBody());
        assertThat(response.getBody().contains("panel panel-default qna-list"), is(true));
    }

    @Test
    public void list_without_login() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body: {}", response.getBody());
        assertThat(response.getBody().contains("panel panel-default qna-list"), is(true));
    }

    @Test
    public void view() {
        Question question = questionRepository.findOne(1L);
        ResponseEntity<String> response = template().getForEntity(question.generateUrl(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body: {}", response.getBody());
        assertThat(response.getBody().contains(question.getTitle()), is(true));
    }

    @Test
    public void modifyForm() {
        Question question = questionRepository.findOne(1L);
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body: {}", response.getBody());
        assertThat(response.getBody().contains(question.getTitle()), is(true));
    }

    @Test
    public void modifyForm_without_login() {
        Question question = questionRepository.findOne(1L);
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void modify() {
        Question question = questionRepository.findOne(1L);
        PostRequestBuilder postBuilder = PostRequestBuilder.urlEncodedHeader();
        postBuilder.addParam("title", "타이틀 수정주우웅");
        postBuilder.addParam("contents", "내용도 수정주우우우웅 '- '?");

        ResponseEntity<String> response = basicAuthTemplate().postForEntity(question.generateUrl(), postBuilder.build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        log.debug("body: {}", response.getBody());
        assertThat(response.getHeaders().getLocation().getPath(), is(question.generateUrl()));

    }

    @Test
    public void modify_without_login() {
        Question question = questionRepository.findOne(1L);
        PostRequestBuilder postBuilder = PostRequestBuilder.urlEncodedHeader();
        postBuilder.addParam("title", "타이틀 수정주우웅");
        postBuilder.addParam("contents", "내용도 수정주우우우웅 '- '?");

        ResponseEntity<String> response = template().postForEntity(question.generateUrl(), postBuilder.build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete() {
        Question question = questionRepository.findOne(1L);
        basicAuthTemplate().delete(question.generateUrl());

        Optional<Question> questionOptional = Optional.ofNullable(questionRepository.findOne(question.getId()));
        assertThat(questionOptional.isPresent(), is(true));
        assertThat(questionOptional.get().isDeleted(), is(true));
    }

    @Test
    public void delete_without_login() {
        Question question = questionRepository.findOne(2L);
        template().delete(question.generateUrl());

        Optional<Question> questionOptional = Optional.ofNullable(questionRepository.findOne(question.getId()));
        assertThat(questionOptional.isPresent(), is(true));
        assertThat(questionOptional.get().isDeleted(), is(false));
    }

    @Test
    public void createForm() {
        User loginUser = defaultUser();

        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body: {}", response.getBody());
    }

    @Test
    public void createForm_without_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void create() {
        String title = "제목입니다.";

        PostRequestBuilder postBuilder = PostRequestBuilder.urlEncodedHeader();
        postBuilder.addParam("title", title);
        postBuilder.addParam("contents", "질문 내용입니다.");

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", postBuilder.build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        log.debug("headers: {}", response.getHeaders());
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
    }

}
