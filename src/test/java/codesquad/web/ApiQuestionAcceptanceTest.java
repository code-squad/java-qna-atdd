package codesquad.web;

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

import static codesquad.domain.QuestionTest.originalQuestion;
import static codesquad.domain.UserTest.newUser;
import static codesquad.domain.UserTest.owner;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);
    private String location;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void setUp() throws Exception {
        originalQuestion.setId(1);
        originalQuestion.writeBy(owner);
        location = createResource("/api/questions", originalQuestion);
    }

    @Test
    public void 질문내용_작성_본인() throws Exception {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", originalQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void 질문내용_작성_로그인X() throws Exception {
        ResponseEntity<Void> response = template().postForEntity("/api/questions", originalQuestion, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문수정_본인() throws Exception {   //로그인 된 경우, 수정버튼 클릭
        Question updatedQuestion = new Question("updatedTitle", "updatedContents");
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updatedQuestion), Question.class);
        log.debug("response body : {}", responseEntity.getBody());
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 질문수정_로그인X() throws Exception {    //로그인 안된 경우, 수정버튼 클릭
        Question updateQuestion = new Question("updatedTitle", "updatedContents");
        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문수정_다른유저() throws Exception {   //다른 로그인 유저일 시, 수정버튼 클릭
        User other = newUser("testUser");
        Question updateQuestion = new Question("updatedTitle", "updatedContents");
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(other).exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문삭제_로그인X() {
        ResponseEntity<Question> responseEntity =
                template().exchange(location, HttpMethod.DELETE, createHttpEntity(originalQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(responseEntity.getBody().isDeleted()).isFalse();
    }

    @Test
    public void 질문삭제_작성자_답변X() {
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

    @Test
    public void 질문삭제_작성자_내_답변만() {
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

    @Test
    public void 질문삭제_작성자_다른_유저_답변() {
        originalQuestion.setId(2);
        originalQuestion.writeBy(owner);
        location = createResource("/api/questions", originalQuestion);

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, HttpEntity.EMPTY, Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문삭제_다른유저() {
        User other = newUser("testUser");
        ResponseEntity<Question> responseEntity =
                basicAuthTemplate(other).exchange(location, HttpMethod.DELETE, createHttpEntity(originalQuestion), Question.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(responseEntity.getBody().isDeleted()).isFalse();
    }
}
