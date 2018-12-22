package codesquad.web;

import codesquad.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;


import static codesquad.domain.QuestionTest.DEFAULT_QUESTION;
import static codesquad.domain.QuestionTest.UPDATED_QUESTION;
import static codesquad.domain.UserTest.SANJIGI;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create() throws Exception {
        String location = createResource("/api/questions", DEFAULT_QUESTION);
        Question dbQuestion = getResource(location, Question.class, defaultUser());
        softly.assertThat(dbQuestion).isNotNull();
    }

    /*  TODO!
     *  ResponseEntity 제네릭 지정에 따라 어떻게 달라지는가? Void vs String vs Object(Question)
     *  중복코드 제거
     *  특정 메서드에 throws Exception 지정하는 이유가? 익셉션 발생은 HttpStatus를 통해 확인하고 있는 게 아닌가? 별개..?
     */

    @Test
    public void show() {
        String location = createResource("/api/questions", DEFAULT_QUESTION);
        softly.assertThat(template().getForEntity(location, Void.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update_정상() throws Exception {
        UPDATED_QUESTION.writeBy(defaultUser());

        String location = createResource("/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Question> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(UPDATED_QUESTION.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_작성자불일치() throws Exception {
        UPDATED_QUESTION.writeBy(defaultUser());
        String location = createResource("/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate(SANJIGI).exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_비로그인() throws Exception {
        String location = createResource("/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Void> responseEntity =
                template().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("error message : {}", responseEntity.getBody());
    }

    @Test
    public void delete_정상() {
        String location = createResource("/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(DEFAULT_QUESTION), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_작성자불일치() {
        String location = createResource("/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Void> responseEntity =
                basicAuthTemplate(SANJIGI).exchange(location, HttpMethod.DELETE, createHttpEntity(DEFAULT_QUESTION), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_비로그인() {
        String location = createResource("/api/questions", DEFAULT_QUESTION);

        ResponseEntity<Void> responseEntity =
                template().exchange(location, HttpMethod.DELETE, createHttpEntity(DEFAULT_QUESTION), Void.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
