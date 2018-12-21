package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiAnswerAcceptanceTest.class);

    private Question question;
    private Answer answer;

    @Before
    public void setUp() throws Exception {
        Question question = new Question();
        question.setId(1);
        question.setTitle("who are we?");
        question.setContents("we are codesquad!");
        this.question = question;

        Answer answer = new Answer();
        answer.setId(1);
        answer.setContents("this is who i am");
        this.answer = answer;
    }

    @Test
    public void 답변_생성_로그인됨() {
        String location =
                createResource("/api/questions/" + question.getId() + "/answers/", "and i am soop.");
        Answer newAnswer = template().getForObject(location, Answer.class);
        softly.assertThat(newAnswer).isNotNull();
    }

    @Test
    public void 답변_생성_로그인안됨() {
        Answer newAnswer = new Answer("should be failed");
        ResponseEntity<Void> response =
                template().postForEntity("/api/questions/" + question.getId() + "/answers/", newAnswer.getContents(), Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 업데이트() {
        String location =
                createResource("/api/questions/" + question.getId() + "/answers", answer.getContents());
        log.debug("answer test : " + location);

        Answer updateAnswer = new Answer(defaultUser(), "i should be changed");
        ResponseEntity<Answer> response =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateAnswer.getContents()), Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().getContents()).isEqualTo(updateAnswer.getContents());
    }

    @Test
    public void 업데이트_로그인_안됨() {
        String location =
                createResource("/api/questions/" + question.getId() + "/answers", answer.getContents());
        log.debug("answer without login : {}", location);
        Answer updateAnswer = new Answer("contents");
        ResponseEntity<Answer> response =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateAnswer.getContents()), Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 업데이트_다른사람() {
        String location =
                createResource("/api/questions/" + question.getId() + "/answers", answer.getContents());
        log.debug("answer the others : {}", location);
        Answer updateAnswer = new Answer("contents");
        ResponseEntity<Answer> response =
                basicAuthAnotherTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateAnswer.getContents()), Answer.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
