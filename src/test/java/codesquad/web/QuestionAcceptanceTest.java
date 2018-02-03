package codesquad.web;

import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.web.utils.HtmlFormDataBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    protected QuestionRepository questionRepository;

    @Autowired
    protected AnswerRepository answerRepository;

    private Question question;

    @Before
    public void setup() {
        Question question = createQuestion("질문1", "질문1입니다.", defaultUser());
        this.question = questionRepository.save(question);
    }

    @After
    public void tearDown() {
        questionRepository.delete(question.getId());
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(question.getTitle());
    }

    @Test
    public void detail() {
        ResponseEntity<String> response = template().getForEntity("/questions/" + question.getId(), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(question.getTitle(), question.getContents());
    }

    @Test
    public void createForm() {
        ResponseEntity<String> response = basicAuthTemplate(question.getWriter())
                .getForEntity(String.format("/questions/form", question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("제목", "내용", "질문하기");
    }

    @Test
    public void createForm_no_login(){
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/form", question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void create() throws Exception{
        ResponseEntity<String> response = create(basicAuthTemplate(defaultUser()));
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/questions/"));
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<String> response = create(template());
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response = basicAuthTemplate(question.getWriter())
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(question.getContents(), question.getTitle());
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateForm_not_writer() {
        User otherUser = new User("moonchan", "password", "moonchan", "moonchan@test.com");
        ResponseEntity<String> response = basicAuthTemplate(otherUser)
                .getForEntity(String.format("/questions/%d/form", question.getId()), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate(defaultUser()), question.getId());
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().equals("/questions/" + question.getId()));
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template(), question.getId());
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_not_writer() throws Exception {
        ResponseEntity<String> response = update(template(), question.getId());
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete() throws Exception {
        ResponseEntity<String> response = delete(basicAuthTemplate(defaultUser()), question.getId());
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().equals("/"));
    }

    @Test
    public void delete_no_login() throws Exception {
        ResponseEntity<String> response = delete(template(), question.getId());
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_not_writer() throws Exception {
        ResponseEntity<String> response = delete(template(), question.getId());
        Assert.assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "질문1")
                .addParameter("contents", "질문1 입니다.")
                .build();

        return template.postForEntity("/questions/", request, String.class);
    }

    private ResponseEntity<String> update(TestRestTemplate template, long questionId) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "put")
                .addParameter("title", "질문2")
                .addParameter("contents", "질문2 입니다.")
                .build();

        return template.postForEntity(String.format("/questions/%d", questionId), request, String.class);
    }

    private ResponseEntity<String> delete(TestRestTemplate template, long questionId) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("_method", "delete")
                .build();
        return template.postForEntity(String.format("/questions/%d", questionId), request, String.class);
    }

    private Question createQuestion(String title, String contents, User writer) {
        Question question = new Question(title, contents);
        question.writeBy(writer);
        return question;
    }

}
