package codesquad.web;

import codesquad.domain.Answer;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.RED;
import static codesquad.domain.UserTest.UNHEE;
import static org.slf4j.LoggerFactory.getLogger;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void create() {
        String contents = "contents";
        String location = createResourceLogin(String.format("/api/questions/%d/answers", 3), contents, RED);
        Answer dbAnswer = template().getForObject(location, Answer.class);
        softly.assertThat(dbAnswer).isNotNull();
    }

    @Test
    public void create_no_login() {
        String contents = "contents";
        ResponseEntity<Void> response = template().postForEntity(String.format("/api/questions/%d/answers", 3), contents, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() {
        String newAnswer = "answers";
        String location = createResourceLogin(String.format("/api/questions/%d/answers", 3), newAnswer, RED);
        Answer original = template().getForObject(location, Answer.class);

        Answer updateAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(),
                "updatecontents");
        ResponseEntity<Answer> responseEntity = basicAuthTemplate(RED).exchange(location, HttpMethod.PUT,
                createHttpEntity(updateAnswer), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("response body : {}", responseEntity.getBody());

    }
    @Test
    public void update_other_writer() {
        String newAnswer = "answers";

        String location = createResourceLogin(String.format("/api/questions/%d/answers", 3), newAnswer, RED);
        Answer original = template().getForObject(location, Answer.class);

        Answer updateAnswer = new Answer(original.getId(), original.getWriter(), original.getQuestion(),
                "updateContents");
        ResponseEntity<Answer> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.PUT,
                createHttpEntity(updateAnswer), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("response body : {}", responseEntity.getBody());
    }

    @Test
    public void update_no_login() {
        String newAnswer = "contents";
        ResponseEntity<Void> response = template().postForEntity(String.format("/api/questions/%d/answers", 3), newAnswer, Void.class);
        log.debug("response body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        String newAnswer = "answers";
        String location = createResourceLogin(String.format("/api/questions/%d/answers", 3), newAnswer, RED);
        Answer original = template().getForObject(location, Answer.class);

        ResponseEntity<Answer> responseEntity = basicAuthTemplate(RED).exchange(location, HttpMethod.DELETE,
                createHttpEntity(original), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("responseEntity : {}", responseEntity.getBody());
    }

    @Test
    public void delete_other_writer() {
        String newAnswer = "answers";
        String location = createResourceLogin(String.format("/api/questions/%d/answers", 3), newAnswer, RED);
        Answer original = template().getForObject(location, Answer.class);

        ResponseEntity<Answer> responseEntity = basicAuthTemplate(UNHEE).exchange(location, HttpMethod.DELETE,
                createHttpEntity(original), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("responseEntity : {}", responseEntity.getBody());
    }

    @Test
    public void delete_no_login() {
        String newAnswer = "answers";
        String location = createResourceLogin(String.format("/api/questions/%d/answers", 3), newAnswer, RED);
        Answer original = template().getForObject(location, Answer.class);

        ResponseEntity<Answer> responseEntity = template().exchange(location, HttpMethod.DELETE,
                createHttpEntity(original), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("resonseEntity: {}", responseEntity.getBody());
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
