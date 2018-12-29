package codesquad.web;

import codesquad.domain.*;
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
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 로그인안했을때질문삭제불가() {
        ResponseEntity<Question> response = template()
                .exchange(String.format("/api/questions/%d", 1), HttpMethod.DELETE, createHttpEntity(QuestionFixture.QUESTION), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 게시글작성자와_로그인유저가같을때_질문삭제가능() {
        // 질문 생성
        String questionLocation = createResource("/api/questions", new Question("안녕하세요", "반갑습니다."));

        // 질문 삭제
        ResponseEntity<Question> response = basicAuthTemplate()
                .exchange(questionLocation, HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Question.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 게시글작성자와_로그인유저가다를때_질문삭제불가() {
        ResponseEntity<Question> response = basicAuthTemplate(findByUserId("master"))
                .exchange(String.format("/api/questions/%d", 1), HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void 질문자와_로그인유저가같고_답변글의_모든답변자_같은경우_게시글삭제가능() {
        // 질문생성
        String questionLocation = createResource("/api/questions", new Question("하이요요용", "내용입니다"));
        logger.debug("asdf : {}", questionLocation);

        // 댓글생성
        ResponseEntity<Question> answerResponse = basicAuthTemplate().postForEntity(questionLocation + "/answers", "댓글입니다.", Question.class);
        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 질문삭제
        ResponseEntity<Question> response = basicAuthTemplate()
                .exchange(questionLocation, HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 질문자와_로그인유저가같고_답변글의_답변자_다른경우_게시글삭제불가() {
        // 질문생성
        String questionLocation = createResource("/api/questions", new Question("하이요요용", "내용입니다"));
        logger.debug("asdf : {}", questionLocation);

        // 댓글생성
        ResponseEntity<Question> answerResponse = basicAuthTemplate(UserFixture.USER_2)
                .postForEntity(questionLocation + "/answers", "댓글입니다.", Question.class);
        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 질문삭제
        ResponseEntity<Question> response = basicAuthTemplate()
                .exchange(questionLocation, HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Question.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
