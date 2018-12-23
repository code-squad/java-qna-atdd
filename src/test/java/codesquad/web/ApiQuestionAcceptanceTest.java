package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.DeleteHistory;
import codesquad.domain.Question;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create() {
        Question newQuestion = new Question("new title", "new contents");
        String location = createResource("/api/questions", newQuestion);
        Question question = template().getForObject(location, Question.class);
        softly.assertThat(question).isNotNull();
    }

    @Test
    public void 생성_로그인_안됨() {
        Question newQuestion = new Question("new title with no login", "new contents with no login");
        ResponseEntity<String> response = template().postForEntity("/api/questions", newQuestion, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 업데이트_로그인됨() {
        Question newQuestion = new Question("title", "contents");
        String location = createResource("/api/questions", newQuestion);

        Question updateQuestion = new Question
                (" update title", "update contents");

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsQuestion(responseEntity.getBody())).isTrue();
    }

    @Test
    public void 업데이트_로그인안됨() {
        Question newQuestion = new Question("title", "contents");
        String location = createResource("/api/questions", newQuestion);

        Question updateQuestion = new Question
                (" update title", "update contents");

        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 업데이트_다른사람() {
        Question newQuestion = new Question("title", "contents");
        String location = createResource("/api/questions", newQuestion);

        Question updateQuestion = new Question
                ("update title", "update contents");

        ResponseEntity<Question> response =
                basicAuthAnotherTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 삭제_로그인됨() {
        Question newQuestion = new Question("title", "content");
        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<Void> response = basicAuthTemplate()
                .exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 삭제_사용자_다를때() {
        Question newQuestion = new Question("title", "content");
        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<Void> response = basicAuthAnotherTemplate()
                .exchange(location, HttpMethod.DELETE, createHttpEntity(null),Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}