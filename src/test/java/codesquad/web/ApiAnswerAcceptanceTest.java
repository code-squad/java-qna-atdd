package codesquad.web;

import codesquad.domain.Answer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static codesquad.domain.AnswerTest.ANSWER;
import static codesquad.domain.UserTest.other;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    public static final String URL = "/api/questions/1/answers/";

    private String location;

    @Before
    public void setUp() throws Exception {
        location = createResource(URL, ANSWER);
    }

    @Test
    public void create_no_login() {
        ResponseEntity<String> response = template().postForEntity(URL, ANSWER, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", response.getBody());
    }

    @Test
    public void create() {
        Answer dbAnswer = basicAuthTemplate().getForObject(location, Answer.class);

        softly.assertThat(dbAnswer).isNotNull();
    }

    @Test
    public void show() {
        softly.assertThat(template().getForEntity(location, String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update_no_login() {
        ResponseEntity<String> responseEntity = updateResponse(template());

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update_not_owner() {
        ResponseEntity<String> responseEntity = updateResponse(basicAuthTemplate(other));

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void update() {
        ResponseEntity<String> responseEntity = updateResponse(basicAuthTemplate());

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void delete_no_login() {
        ResponseEntity<String> responseEntity = deleteResponse(template());
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_not_owner() {
        ResponseEntity<String> responseEntity = deleteResponse(basicAuthTemplate(other));
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        ResponseEntity<String> responseEntity = deleteResponse(basicAuthTemplate());
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private ResponseEntity<String> updateResponse(TestRestTemplate testRestTemplate) {
        return testRestTemplate.exchange(location, HttpMethod.PUT, createHttpEntity(ANSWER), String.class);
    }

    private ResponseEntity<String> deleteResponse(TestRestTemplate testRestTemplate) {
        return testRestTemplate.exchange(location, HttpMethod.DELETE, createHttpEntity(null), String.class);
    }
}
