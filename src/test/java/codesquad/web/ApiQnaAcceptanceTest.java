package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import support.test.AcceptanceTest;

public class ApiQnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQnaAcceptanceTest.class);

    @Test
    public void create() throws Exception {
        Question newQuestion = new Question("ttt", "ccc");
        String location = createResourceByDefaultUser("/api/questions", newQuestion);

        Question dbQuestion = getResource(location, Question.class);
        softly.assertThat(dbQuestion.getTitle()).isEqualTo(newQuestion.getTitle());
        softly.assertThat(dbQuestion.getContents()).isEqualTo(newQuestion.getContents());
        softly.assertThat(dbQuestion.getWriter()).isEqualTo(defaultUser());
    }

    @Test
    public void create_로그인_안함() throws Exception {
        ResponseEntity<Void> response = template()
                .postForEntity("/api/questions/", new Question("ttt", "ccc"), Void.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void show() throws Exception {
        Question dbQuestion = getResource("/api/questions/1", Question.class);
        softly.assertThat(dbQuestion).isEqualTo(defaultQuestion());
    }

    @Test
    public void update() {
        Question newQuestion = new Question("new title", "new contents");
        String location = createResourceByDefaultUser("/api/questions", newQuestion);

        Question updateQuestion = new Question("update title", "update contents");
        ResponseEntity<Question> responseEntity = basicAuthTemplate().
                exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(updateQuestion.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void createAnswer() {
        String newContents = "new Contents";
        String location = createResourceByDefaultUser("/api/questions/1/answers", newContents);

        Answer savedAnswer = getResource(location, Answer.class);
        softly.assertThat(savedAnswer.getContents()).isEqualTo(newContents);
    }

    @Test
    public void updateAnswer(){
        String location = createResourceByDefaultUser("/api/questions/1/answers", "new contents");

        String updateContents = "update contents";
        ResponseEntity<Answer> responseEntity = basicAuthTemplate()
                .exchange(location, HttpMethod.PUT, createHttpEntity(updateContents), Answer.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().getContents()).isEqualTo(updateContents);
    }

    private String createResourceByDefaultUser(String url, Object bodyPayload) {
        ResponseEntity<Void> response =
                basicAuthTemplate().postForEntity(url, bodyPayload, Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getHeaders().getLocation().getPath();

    }

    private <T> T getResource(String url, Class<T> type) {
        return template().getForObject(url, type);
    }

    private HttpEntity createHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, headers);
    }
}
