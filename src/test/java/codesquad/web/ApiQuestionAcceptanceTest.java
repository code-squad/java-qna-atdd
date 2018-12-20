package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionFixture;
import codesquad.domain.UserFixture;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void 로그인안했을때질문못함() {
        ResponseEntity<Void> response = template()
                .postForEntity("/api/questions", QuestionFixture.QUESTION, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인했을때질문가능() {
        ResponseEntity<Void> response = basicAuthTemplate()
                .postForEntity("/api/questions", QuestionFixture.QUESTION, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void 로그인했을때질문글자수_3자리미만일때_에러() {
        ResponseEntity<Void> response = basicAuthTemplate()
                .postForEntity("/api/questions", QuestionFixture.ERROR_QUESTION, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 로그인안했을때_질문수정불가() {
        ResponseEntity<Question> response = template()
                .exchange(String.format("/api/questions/%d", 1), HttpMethod.PUT, createHttpEntity(QuestionFixture.QUESTION), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }

    @Test
    public void 게시글작성자와_로그인유저가같을때_질문수정가능() {
        ResponseEntity<Question> response = basicAuthTemplate()
                .exchange(String.format("/api/questions/%d", 1), HttpMethod.PUT, createHttpEntity(QuestionFixture.QUESTION), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 게시글작성자와_로그인유저가다를때_질문수정불가() {
        ResponseEntity<Question> response = basicAuthTemplate(UserFixture.USER_2)
                .exchange(String.format("/api/questions/%d", 1), HttpMethod.PUT, createHttpEntity(QuestionFixture.QUESTION), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인안했을때질문삭제불가() {
        ResponseEntity<Question> response = template()
                .exchange(String.format("/api/questions/%d", 1), HttpMethod.DELETE, createHttpEntity(QuestionFixture.QUESTION), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 게시글작성자와_로그인유저가같을때_질문삭제가능() {
        ResponseEntity<Question> response = basicAuthTemplate()
                .exchange(String.format("/api/questions/%d", 1), HttpMethod.DELETE, createHttpEntity(QuestionFixture.QUESTION), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 게시글작성자와_로그인유저가다를때_질문삭제불가() {
        ResponseEntity<Question> response = basicAuthTemplate(findByUserId("master"))
                .exchange(String.format("/api/questions/%d", 1), HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
