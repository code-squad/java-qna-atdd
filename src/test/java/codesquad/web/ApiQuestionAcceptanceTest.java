package codesquad.web;

import codesquad.domain.Question;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static codesquad.domain.QuestionTest.newQuestion;
import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LogManager.getLogger(ApiQuestionAcceptanceTest.class);
    private static final String URL = "/api/questions";

    private static String location;
    private static Question question;

    @Before
    public void setUp() {
        question = newQuestion(JAVAJIGI);
        location = createResource(URL, question);
    }

    @Test
    public void create() {
        Question dbQuestion = basicAuthTemplate().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
    }

    @Test
    public void update() {
        ResponseEntity<String> responseEntity = updateSetup(basicAuthTemplate());

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> responseEntity = updateSetup(template());

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());

    }


    @Test
    public void update_other_login() {
        ResponseEntity<String> responseEntity = updateSetup(basicAuthTemplate(SANJIGI));

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("error message : {}", responseEntity.getBody());

    }

    private ResponseEntity<String> updateSetup(TestRestTemplate testRestTemplate) {
        Question updateQuestion = newQuestion("홍홍홍", "사사사");
        return testRestTemplate.exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), String.class);
    }

}
