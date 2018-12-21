package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import javax.annotation.Resource;

import static codesquad.domain.UserTest.RED;
import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiQuestionAcceptanceTest.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Test
    public void create() throws Exception {
        Question newQuestion = new Question("title", "contents");;
        String location = createResourceLogin("/api/questions", newQuestion, RED);
        Question dbQuestion = template().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
        log.debug("dbQuestion : {}", dbQuestion);
    }

    @Test
    public void create_no_login() throws Exception {
        Question newQuestion = new Question(RED, "createTest Title", "createTest Contents");
        ResponseEntity<String> response = template().postForEntity("/api/questions", newQuestion, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("response : {}", response);
    }

    @Test
    public void update() throws Exception {
        Question newQuestion = new Question("title", "contents");
        String location = createResourceLogin("/api/questions", newQuestion, defaultUser());

        Question original = template().getForObject(location, Question.class);

        Question updateQuestion = new Question(original.getWriter(), original.getTitle(), "updateContents");
        ResponseEntity<Question> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.PUT,
                createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("updateQuestion  : {}", updateQuestion);
        log.debug("response body : {}", responseEntity.getBody());
        softly.assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();

    }

    @Test
    public void update_no_login() {
        Question newQuestion = new Question("title", "contents");
        String location = createResourceLogin("/api/questions", newQuestion, defaultUser());

        Question original = template().getForObject(location, Question.class);

        Question updateQuestion = new Question(original.getWriter(), original.getTitle(), "updateContents");
        ResponseEntity<Question> responseEntity = template().exchange(location, HttpMethod.PUT,
                createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("update_no_login() response : {}", responseEntity.getBody());
    }

    @Test
    public void update_other_writer() throws Exception {
        Question newQuestion = new Question("Title", "Contents");
        ResponseEntity<Void> response = basicAuthTemplate(RED).postForEntity("/api/questions", newQuestion, Void.class);
        log.debug("response : {}", response);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String location = response.getHeaders().getLocation().getPath();
        Question original = template().getForObject(location, Question.class);


        Question updateQuestion = new Question(original.getWriter(), original.getTitle(), "updateContents");
        ResponseEntity<Question> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.PUT,
                createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("responetEntity : {}", responseEntity.getBody());

    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
