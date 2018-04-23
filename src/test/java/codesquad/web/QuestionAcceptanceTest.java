package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.hamcrest.CoreMatchers;
import org.junit.After;
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
import support.html.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author sangsik.kim
 */
public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);
    private Question testQuestion;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    UserRepository userRepository;


    @Before
    public void setup() {
        testQuestion = questionRepository.save(new Question("테스트 제목", "테스트 내용", defaultUser()));
    }

    @After
    public void teardown() {
        questionRepository.delete(testQuestion);
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getBody().contains(testQuestion.getTitle()), is(Boolean.TRUE));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void show() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", testQuestion.getId()), String.class);

        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        assertThat(response.getBody().contains(testQuestion.getTitle()), CoreMatchers.is(Boolean.TRUE));
        assertThat(response.getBody().contains(testQuestion.getContents()), CoreMatchers.is(Boolean.TRUE));
    }

    @Test
    public void form_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void form_login() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create_login() {
        ResponseEntity<String> response = create(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
    }

    @Test
    public void create_no_login() {
        ResponseEntity<String> response = create(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> create(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("title", "안녕하세요")
                .addParameter("contents", "반갑습니다")
                .build();

        return template.postForEntity("/questions", request, String.class);
    }

    @Test
    public void update_form_no_login() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", testQuestion.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_form_not_author() {
        User anotherUser = userRepository.findByUserId("sanjigi").get();
        ResponseEntity<String> response = basicAuthTemplate(anotherUser).getForEntity(String.format("/questions/%d/form", testQuestion.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_form_author() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", testQuestion.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(testQuestion.getTitle()), is(Boolean.TRUE));
        assertThat(response.getBody().contains(testQuestion.getContents()), is(Boolean.TRUE));
    }

    @Test
    public void update() {
        ResponseEntity<String> response = update(basicAuthTemplate());
        Question updatedQuestion = questionRepository.findOne(testQuestion.getId());

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(updatedQuestion.getTitle(), is("제목 수정 테스트"));
        assertThat(updatedQuestion.getContents(), is("내용 수정 테스트"));
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_not_author() {
        User user = userRepository.findByUserId("sanjigi").get();
        ResponseEntity<String> response = update(basicAuthTemplate(user));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder
                .urlEncodedForm()
                .put()
                .addParameter("title", "제목 수정 테스트")
                .addParameter("contents", "내용 수정 테스트")
                .build();

        return template.postForEntity(String.format("/questions/%d", testQuestion.getId()), request, String.class);
    }

    @Test
    public void delete_no_login() {
        ResponseEntity<String> response = delete(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_not_author() {
        User user = userRepository.findByUserId("sanjigi").get();
        ResponseEntity<String> response = delete(basicAuthTemplate(user));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete() {
        ResponseEntity<String> response = delete(basicAuthTemplate());
        Question deletedQuestion = questionRepository.findOne(testQuestion.getId());

        assertThat(deletedQuestion.isDeleted(), is(Boolean.TRUE));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
    }

    private ResponseEntity<String> delete(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("_method", "delete")
                .build();
        return template.postForEntity(String.format("/questions/%d", testQuestion.getId()), request, String.class);
    }
}
