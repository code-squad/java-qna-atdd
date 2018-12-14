package codesquad.web;

import codesquad.domain.DeleteHistory;
import codesquad.domain.DeleteHistoryRepository;
import codesquad.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.RestJsonDataBuilder;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    private static RestJsonDataBuilder restJsonDataBuilder;

    @Autowired
    DeleteHistoryRepository deleteHistoryRepository;

    @Test
    public void create() {
        Question question = new Question("title", "contents");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions");
        restJsonDataBuilder.createEntity(basicAuthTemplate(defaultUser()), question, String.class);

        Question dbQuestion = restJsonDataBuilder.getResource(template(), Question.class);
        logger.debug("dbQuestion : {}", dbQuestion);
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void update() {
        Question question = new Question("title", "contents");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions");
        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(basicAuthTemplate(defaultUser()), question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Question updateQuestion = new Question("title2", "contents2");

        ResponseEntity<Question> responseEntity = restJsonDataBuilder
                .updateEntity(basicAuthTemplate(defaultUser()), updateQuestion, Question.class);

        Question dbQuestion = restJsonDataBuilder.getResource(template(), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(dbQuestion.getTitle()).isEqualTo(responseEntity.getBody().getTitle());
    }

    @Test
    public void update_no_login() {
        Question question = new Question("title", "contents");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions");
        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(basicAuthTemplate(defaultUser()), question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Question updateQuestion = new Question("title2", "contents2");

        ResponseEntity<String> responseEntity = restJsonDataBuilder.updateEntity(template(), updateQuestion, String.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        logger.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_by_another_user() {
        Question question = new Question("title", "contents");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions");
        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(basicAuthTemplate(defaultUser()), question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Question updateQuestion = new Question("title2", "contents2");

        ResponseEntity<Void> responseEntity =
                restJsonDataBuilder.updateEntity(basicAuthTemplate(secondUser()), updateQuestion, Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_has_no_answers() {
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions");
        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(basicAuthTemplate(), new Question("title", "contents"), Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> responseEntity = restJsonDataBuilder.deleteEntity(basicAuthTemplate(), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_has_answer() {
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions/2");

        ResponseEntity<Void> responseEntity = restJsonDataBuilder.deleteEntity(basicAuthTemplate(secondUser()), Void.class);
        for (DeleteHistory deleteHistory : deleteHistoryRepository.findAll()) {
            logger.debug("deleteHistory : {}", deleteHistory);
        }

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_no_login() {
        Question question = new Question("title", "contents");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions");
        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(basicAuthTemplate(defaultUser()), question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> responseEntity = restJsonDataBuilder.deleteEntity(template(), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_other_user() {
        Question question = new Question("title", "contents");
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions");
        ResponseEntity<Void> response = restJsonDataBuilder.createEntity(basicAuthTemplate(defaultUser()), question, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> responseEntity = restJsonDataBuilder.deleteEntity(basicAuthTemplate(secondUser()), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_cannot_answers() {
        restJsonDataBuilder = new RestJsonDataBuilder("/api/questions/1");

        ResponseEntity<Void> responseEntity = restJsonDataBuilder.deleteEntity(basicAuthTemplate(), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}


