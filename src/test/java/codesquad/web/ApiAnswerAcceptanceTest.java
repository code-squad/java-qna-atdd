package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionFixture;
import codesquad.domain.UserFixture;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = getLogger(ApiAnswerAcceptanceTest.class);
    private static final String ANSWER_URL = "/answers";

    @Test
    public void 로그인안했을때_댓글안됨() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", QuestionFixture.QUESTION, Void.class);
        String location = response.getHeaders().getLocation().getPath() + ANSWER_URL;
        ResponseEntity<Void> responseEntity = template()
                .postForEntity(location,"컨텐츠작성함", Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인했을때_댓글가능() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", QuestionFixture.QUESTION, Void.class);
        String location = response.getHeaders().getLocation().getPath() + ANSWER_URL;
        ResponseEntity<Void> responseEntity = basicAuthTemplate()
                .postForEntity(location,"컨텐츠작성", Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void 댓글_글자수5자미만이면_작성안됨() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", QuestionFixture.QUESTION, Void.class);
        String location = response.getHeaders().getLocation().getPath() + ANSWER_URL;
        ResponseEntity<Void> responseEntity = basicAuthTemplate()
                .postForEntity(location,"컨텐츠작", Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void 로그인안했을때_댓글삭제안됨() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", QuestionFixture.QUESTION, Void.class);
        String location = response.getHeaders().getLocation().getPath() + ANSWER_URL;
        Answer answer = new Answer(UserFixture.USER_2, "컨텐츠작성");

        ResponseEntity<Void> responseEntity = basicAuthTemplate()
                .postForEntity(location, answer, Void.class);

        ResponseEntity<Void> responseEntity1 = template()
                .exchange(String.format(location + "/%d", answer.getId()), HttpMethod.DELETE, createHttpEntity(null), Void.class);

        softly.assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 로그인유저와_댓글작성자가_다를때_댓글삭제안됨() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", QuestionFixture.QUESTION, Void.class);
        String location = response.getHeaders().getLocation().getPath() + ANSWER_URL;
        Answer answer = new Answer(UserFixture.USER_2, "컨텐츠작성");
        answer.setId(1);

        ResponseEntity<Void> responseEntity = basicAuthTemplate(findByUserId("sanjigi"))
                .postForEntity(location, answer, Void.class);

        ResponseEntity<Void> responseEntity1 = basicAuthTemplate(UserFixture.USER)
                .exchange(String.format(location + "/%d", answer.getId()), HttpMethod.DELETE, createHttpEntity(null), Void.class);

        softly.assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 로그인유저와_댓글작성자가_같을때_댓글삭제가능() {
        ResponseEntity<Void> response = basicAuthTemplate().postForEntity("/api/questions", QuestionFixture.QUESTION, Void.class);
        String location = response.getHeaders().getLocation().getPath() + ANSWER_URL;

        Answer answer = new Answer(UserFixture.USER, "컨텐츠작성");
        ResponseEntity<Void> responseEntity = basicAuthTemplate()
                .postForEntity(location, answer, Void.class);

        ResponseEntity<Void> responseEntity1 = basicAuthTemplate(UserFixture.USER)
                .exchange(String.format(location + "/%d", answer.getId()), HttpMethod.DELETE, createHttpEntity(null), Void.class);

        softly.assertThat(responseEntity1.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
