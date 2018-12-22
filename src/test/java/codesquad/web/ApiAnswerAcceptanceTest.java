package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.newUser;


public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private User owner;
    private Question originalQuestion;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        owner = defaultUser();
        originalQuestion = new Question("title", "contents");
        originalQuestion.setId(1);
        originalQuestion.writeBy(owner);
    }

    @Test
    public void 답변생성_로그인X() {
        Answer answer = new Answer(0L, null, originalQuestion, "contents");
        ResponseEntity<Void> response = template().postForEntity("/api/questions/1/answers", answer, Void.class);
        log.debug("response-------------------- : {} ", response);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변생성_로그인O() {
        Answer answer = new Answer(0L, owner, originalQuestion, "contents");
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions/1/answers", answer, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void 답변삭제_로그인X() {
        Answer answer = new Answer(1L, owner, originalQuestion, "contents");
        String location = createResource("/api/questions/1/answers", answer);
        ResponseEntity<Answer> responseEntity =
                template().exchange(location, HttpMethod.DELETE, createHttpEntity(answer), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변삭제_본인() {
        Answer answer = new Answer(1L, owner, originalQuestion, "contents");
        String location = createResource("/api/questions/1/answers", answer);
        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(answer), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("responseEntity---------------------- : {}", responseEntity);
    }

    @Test
    public void 답변삭제_다른유저() {
        User other = newUser("testUser");
        Answer answer = new Answer(owner, "contents");
        String location = createResource("/api/questions/1/answers", answer);
        ResponseEntity<Answer> responseEntity =
                basicAuthTemplate(other).exchange(location, HttpMethod.DELETE, createHttpEntity(answer), Answer.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
