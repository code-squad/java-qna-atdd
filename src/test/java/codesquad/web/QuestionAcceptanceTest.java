package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.UserRepository;
import com.sun.deploy.net.HttpResponse;
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

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class QuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    private final Long testQuestionNum = Long.valueOf(1);
    private final Long testDeleteNum = Long.valueOf(2);

    @Test
    public void form() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void form_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void show() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", testQuestionNum), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(questionRepository.findById(testQuestionNum).get().getTitle()), is(true));
    }

    @Test
    public void create_no_login() {
        ResponseEntity<String> response = create(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void create_login() {
        long count = questionRepository.count();
        ResponseEntity<String> response = create(basicAuthTemplate());
        log.debug("response : {}", response);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(questionRepository.count(), is(count+1));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/"));
    }

    private ResponseEntity<String> create(TestRestTemplate template) {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        builder.addParameter("title", "test");
        builder.addParameter("contents", "test");

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        return template.postForEntity("/questions", request, String.class);
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", testQuestionNum), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", testQuestionNum), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void update_login() {
        ResponseEntity<String> response = update(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));

        Question question = questionRepository.findById(testQuestionNum).get();
        assertThat(question.getTitle(), is("test2"));
        assertThat(question.getContents(), is("test"));
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> response = update(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        String title = "test2";
        builder.addParameter("title", title);
        builder.addParameter("contents", "test");
        builder.addParameter("_method", "put");

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        return template.postForEntity(String.format("/questions/%d", testQuestionNum), request, String.class);
    }

    @Test
    public void delete_login() {
        ResponseEntity<String> response = delete(basicAuthTemplate(userRepository.findByUserId("sanjigi").get()));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(questionRepository.findById(testDeleteNum), is(Optional.empty()));
    }

    @Test
    public void delete_no_login() {
        ResponseEntity<String> response = delete(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        assertNotNull(questionRepository.findById(testDeleteNum));
    }

    private ResponseEntity<String> delete(TestRestTemplate template) {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("_method", "delete");

        HttpEntity<MultiValueMap<String, Object>> request = builder.build();
        return template.postForEntity(String.format("/questions/%d", testDeleteNum), request, String.class);
    }
}
