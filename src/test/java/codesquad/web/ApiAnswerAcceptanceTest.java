package codesquad.web;

import codesquad.domain.AnswerFixture;
import codesquad.domain.QuestionFixture;
import codesquad.domain.UserFixture;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import static org.slf4j.LoggerFactory.getLogger;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final Logger logger = getLogger(ApiAnswerAcceptanceTest.class);

    private static final String URL = "/answers";

    private static final String QUESTION_LOCATION = "/api/questions";

    private TestRestTemplate testRestTemplateOneSelf;

    private TestRestTemplate testRestTemplateOthers;

    @Before
    public void setUp() {
        testRestTemplateOneSelf = basicAuthTemplate(UserFixture.JAVAJIGI_USER);
        testRestTemplateOthers = basicAuthTemplate(UserFixture.SANJIGI_USER);
    }

    @Test
    public void 답변추가_로그인X() {
        String location = getLocation(postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION));
        ResponseEntity<Void> responseEntity = postResource(template(), location + URL, AnswerFixture.TEST_ANSWER);
        softly.assertThat(getStatusCode(responseEntity)).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변추가_로그인O_본인O_글자수5자리미만() {
        String location = getLocation(postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION));
        ResponseEntity<Void> responseEntity = postResource(testRestTemplateOneSelf, location + URL, AnswerFixture.ERROR_ANSWER);
        softly.assertThat(getStatusCode(responseEntity)).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void 답변추가_로그인O_글자수5자리이상() {
        String location = getLocation(postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION));
        ResponseEntity<Void> responseEntity = postResource(testRestTemplateOneSelf, location + URL, AnswerFixture.TEST_ANSWER);
        softly.assertThat(getStatusCode(responseEntity)).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void 답변삭제_로그인X() {
        String location = getLocation(postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION));
        ResponseEntity<Void> responseEntity = exchangeResource(template(), location + URL, HttpMethod.DELETE, AnswerFixture.TEST_ANSWER);
        softly.assertThat(getStatusCode(responseEntity)).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변삭제_로그인O_본인X() {
        String location = getLocation(postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION));
        ResponseEntity<Void> responseEntity = exchangeResource(basicAuthTemplate(UserFixture.SANJIGI_USER), location + URL,
                HttpMethod.DELETE, AnswerFixture.TEST_ANSWER);
        softly.assertThat(getStatusCode(responseEntity)).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 답변삭제_로그인O_본인O() {
        String location = getLocation(postResource(basicAuthTemplate(), QUESTION_LOCATION, QuestionFixture.TEST_QUESTION));
        ResponseEntity<Void> responseEntity = exchangeResource(basicAuthTemplate(), location + URL,
                HttpMethod.DELETE, AnswerFixture.TEST_ANSWER);
        softly.assertThat(getStatusCode(responseEntity)).isEqualTo(HttpStatus.CREATED);
    }
}
