package codesquad.web;

import codesquad.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create() {
        String location = createResource("/api/questions", createQuestion());
        log.debug("location : {}", location);               // 질문만듦

        Question dbQuestion = template().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
        log.debug("dbQuestion : {}", dbQuestion);
    }

    @Test
    public void craete_no_login() {
        ResponseEntity<Void> response = template().postForEntity("/api/questions", createQuestion(), Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    //getForObject : 객체만 반환
    //getForEntity : 객체 + header정보

    @Test
    public void show() {
        String location = createResource("/api/questions", createQuestion());
        softly.assertThat(template().getForEntity(location, Question.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update() {
        String location = createResource("/api/questions", updateQuestion());

        ResponseEntity<Question> responseEntity
                = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion()), Question.class);

        log.debug("response : {}", responseEntity.getBody());
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().equalsWriter(findByUserId("javajigi"))).isTrue();
    }

    @Test
    public void update_other_user() {
        String location = createResource("/api/questions", updateQuestion());

        ResponseEntity<Question> responseEntity
                = basicAuthTemplate(findByUserId("sanjigi")).exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion()), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_no_login() {
        String location = createResource("/api/questions", createQuestion());

        ResponseEntity<Question> responseEntity
                = template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion()), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }


}
