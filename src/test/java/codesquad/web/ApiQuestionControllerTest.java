package codesquad.web;

import codesquad.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static codesquad.domain.QuestionTest.UPDATED_QUESTION;
import static codesquad.domain.UserTest.JUNGHYUN;
import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionControllerTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiQuestionControllerTest.class);

    @Test
    public void create() {
        Question newQuestion = new Question("테스트 질문1", "테스트 내용1");
        String location = createResource("/api/questions", newQuestion);
        Question dbQuestion = template().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
        log.info("dbQuestion : " + dbQuestion);
    }

    @Test
    public void create_no_login() {
        Question newQuestion = new Question("테스트 질문2", "테스트 내용2");
        ResponseEntity<String> response = template().postForEntity("/api/questions", newQuestion, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("errorMessage : {}", response.getBody());
    }

    @Test
    public void update() {
        Question newQuestion = new Question("테스트 질문3", "테스트 내용3");
        String location = createResource("/api/questions", newQuestion);
        Question original = template().getForObject(location, Question.class);

        ResponseEntity<Question> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(UPDATED_QUESTION.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() {
        Question newQuestion = new Question("테스트 질문4", "테스트 내용4");
        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<Void> responseEntity = template().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_other_user() {
        Question newQuestion = new Question("테스트 질문5", "테스트 내용5");
        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<Void> responseEntity = basicAuthTemplate(JUNGHYUN).exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}