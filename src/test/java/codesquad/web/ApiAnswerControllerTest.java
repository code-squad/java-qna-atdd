package codesquad.web;

import codesquad.domain.Answer;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.domain.ErrorMessage;
import support.test.AcceptanceTest;

import static codesquad.domain.AnswerTest.ANSWER;
import static codesquad.domain.QuestionTest.QUESTION;
import static codesquad.domain.UserTest.JUNGHYUN;
import static codesquad.service.QnaServiceTest.UPDATED_CONTENTS;
import static org.slf4j.LoggerFactory.getLogger;

public class ApiAnswerControllerTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiAnswerControllerTest.class);

    @Test
    public void create() {
        String location = createResource("/api/questions/" + QUESTION.getId() + "/answers", ANSWER.getContents());
        Answer dbAnswer = template().getForObject(location, Answer.class);
        softly.assertThat(dbAnswer).isNotNull();
        log.debug("dbAnswer : " + dbAnswer);
    }

    @Test
    public void update() {
        String location = createResource("/api/questions/" + QUESTION.getId() + "/answers", ANSWER.getContents());
        ResponseEntity<Answer> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_CONTENTS), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().isSameContents(UPDATED_CONTENTS)).isTrue();
    }

    @Test
    public void update_no_login() {
        String location = createResource("/api/questions/" + QUESTION.getId() + "/answers", ANSWER.getContents());
        ResponseEntity<String> responseEntity = template().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_CONTENTS), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error Message : {}", responseEntity.getBody());
    }

    @Test
    public void update_other_user() {
        String location = createResource("/api/questions/" + QUESTION.getId() + "/answers", ANSWER.getContents());
        ResponseEntity<Void> responseEntity = basicAuthTemplate(JUNGHYUN).exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_CONTENTS), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete() {
        String location = createResource("/api/questions/" + QUESTION.getId() + "/answers", ANSWER.getContents());
        ResponseEntity<Answer> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().isDeleted()).isEqualTo(true);
        log.debug("deleted Answer : " + responseEntity.getBody());
    }

    @Test
    public void delete_no_login() {
        String location = createResource("/api/questions/" + QUESTION.getId() + "/answers", ANSWER.getContents());
        ResponseEntity<String> responseEntity = template().exchange(location, HttpMethod.DELETE, createHttpEntity(null), String.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error Message : " + responseEntity.getBody());
    }

    @Test
    public void delete_other_user() {
        String location = createResource("/api/questions/" + QUESTION.getId() + "/answers", ANSWER.getContents());
        ResponseEntity<Void> responseEntity = basicAuthTemplate(JUNGHYUN).exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}