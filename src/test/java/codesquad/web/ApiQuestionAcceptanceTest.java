package codesquad.web;

import codesquad.domain.AnswerFixture;
import codesquad.domain.Question;
import codesquad.domain.QuestionFixture;
import codesquad.domain.UserFixture;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import javax.xml.bind.ValidationException;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger logger = getLogger(ApiQuestionAcceptanceTest.class);

    private static final String QUESTION_LOCATION = "/api/questions";

    @Test
    public void 질문하기_로그인O_Test() {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        softly.assertThat(getStatusCode(responseEntity)).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void 질문하기_로그인X_Test() {
        ResponseEntity<Void> responseEntity = postResource(template(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        softly.assertThat(getStatusCode(responseEntity)).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문하기_로그인O_글자수3미만_Test() {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.ERROR_QUESTION);
        softly.assertThat(getStatusCode(responseEntity)).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void 질문상세보기_Test() {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        String location = responseEntity.getHeaders().getLocation().getPath();
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Question> response = template().getForEntity(location, Question.class);
        softly.assertThat(response.getBody().isTitleAndContentsAndWriter(QuestionFixture.TEST_QUESTION)).isTrue();
    }

    @Test
    public void 질문수정_로그인X_Test() throws ValidationException {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        String location = getLocation(responseEntity);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> response = exchangeResource(template(), location, HttpMethod.PUT, QuestionFixture.UPDATE_QUESTION);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문수정_로그인O_본인X_Test() throws ValidationException {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        String location = getLocation(responseEntity);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> response = exchangeResource(basicAuthTemplate(UserFixture.SANJIGI_USER), location
                , HttpMethod.PUT, QuestionFixture.UPDATE_QUESTION);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문수정_로그인O_본인O_Test() throws ValidationException {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        String location = getLocation(responseEntity);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> response = exchangeResource(basicAuthTemplate(), location, HttpMethod.PUT, QuestionFixture.UPDATE_QUESTION);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(location);
    }

    @Test
    public void 질문삭제_로그인X_Test() throws ValidationException {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        String location = getLocation(responseEntity);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> response = exchangeResource(template(), location, HttpMethod.DELETE, null);
        softly.assertThat(getStatusCode(response)).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문삭제_로그인O_본인X_Test() throws ValidationException {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        String location = getLocation(responseEntity);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> response = exchangeResource(basicAuthTemplate(UserFixture.SANJIGI_USER), location, HttpMethod.DELETE, null);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 질문삭제_로그인O_본인O_Test() throws ValidationException {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        String location = getLocation(responseEntity);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Void> response = exchangeResource(basicAuthTemplate(), location, HttpMethod.DELETE, null);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 질문삭제_로그인O_본인O_질문답변작성자일치O_Test() throws ValidationException {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        String location = getLocation(responseEntity);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        responseEntity = postResource(basicAuthTemplate(), location + "/answers", AnswerFixture.TEST_ANSWER);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        responseEntity = exchangeResource(basicAuthTemplate(), location, HttpMethod.DELETE, AnswerFixture.TEST_ANSWER);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 질문삭제_로그인O_본인O_질문답변작성자일치X_Test() throws ValidationException {
        ResponseEntity<Void> responseEntity = postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION);
        String location = getLocation(responseEntity);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        responseEntity = postResource(basicAuthTemplate(UserFixture.SANJIGI_USER), location + "/answers", AnswerFixture.TEST_ANSWER_SANJIGI);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        logger.debug("Answer location : {}", responseEntity.getHeaders().getLocation());

        ResponseEntity<Void> response = exchangeResource(basicAuthTemplate(), location, HttpMethod.DELETE, AnswerFixture.TEST_ANSWER);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
