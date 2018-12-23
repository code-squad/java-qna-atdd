package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionControllerTest extends AcceptanceTest {
    private static final Logger logger = getLogger(ApiQuestionControllerTest.class);
    private static final Question NEW_QUESTION = new Question("newTitle", "newContents");
    private static final String CREATE_PATH = "/api/questions";

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void create() {
        Question question = new Question("createTest", "createTest");
        createResource(CREATE_PATH, question);
        softly.assertThat(questionRepository.findByTitle("createTest").isPresent()).isTrue();
    }

    @Test
    public void createWithoutLogin() {
        Question newQuestion = new Question("newTitle", "newContents");
        ResponseEntity<Void> response = template().postForEntity("/api/questions", newQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show() {
        long id = 1L;
        ResponseEntity<Question> response = template().getForEntity("/api/questions/" + id, Question.class);
        logger.debug("get Question : {}", response.getBody());
        softly.assertThat(response.getBody().getId()).isEqualTo(id); 
    }

    @Test
    public void update() {
        String location = createResource(CREATE_PATH, NEW_QUESTION);
        logger.debug("location : {} ", location);

        Question updateQuestion = new Question("newnewTitle", "newnewContents");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        logger.debug("responseEntity body : {}", responseEntity.getBody());
        softly.assertThat(responseEntity.getBody().getTitle()).isEqualTo(updateQuestion.getTitle());
        softly.assertThat(responseEntity.getBody().getContents()).isEqualTo(updateQuestion.getContents());
    }

    @Test
    public void updateWithOtherUser() {
        String location = createResource(CREATE_PATH, NEW_QUESTION);
        logger.debug("location : {} ", location);

        Question updateQuestion = new Question("newnewTitle", "newnewContents");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(sanjigiUser()).exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateWithoutLogin() {
        String location = createResource(CREATE_PATH, NEW_QUESTION);
        logger.debug("location : {} ", location);

        Question updateQuestion = new Question("newnewTitle", "newnewContents");

        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        Question question = new Question("forDeleteQuestion", "1234");
        String location = createResource(CREATE_PATH, question);
        logger.debug("location : {} ", location);

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);

        logger.debug("responseEntity body : {}", responseEntity.getBody());
        softly.assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

    @Test
    public void deleteWithoutLogin() {
        String location = createResource(CREATE_PATH, NEW_QUESTION);
        logger.debug("location : {} ", location);

        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);

        logger.debug("responseEntity body : {}", responseEntity.getBody());
        softly.assertThat(responseEntity.getBody().isDeleted()).isFalse();
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void deleteWithOtherUser() {
        String location = createResource(CREATE_PATH, NEW_QUESTION);
        logger.debug("location : {} ", location);

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(sanjigiUser()).exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);

        logger.debug("responseEntity body : {}", responseEntity.getBody());
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}