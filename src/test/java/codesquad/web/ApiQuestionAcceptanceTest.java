package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiQuestionAcceptanceTest.class);

    protected Question createQuestion() {
        return new Question("제목입니다.", "내용입니다.");
    }

    protected Question updateQuestion() {
        return new Question("업데이트 제목", "업데이트 내용");
    }

    @Test
    public void create() {
        String location = createResource("/api/questions", createQuestion());
        log.debug("location : {}", location);               // 질문만듦

        Question dbQuestion = template().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
        log.debug("dbQuestion : {}", dbQuestion);
    }

    @Test
    public void craete_no_login() {
        ResponseEntity<Void> response = template().postForEntity("/api/questions", createQuestion(), Void.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    //getForObject : 객체만 반환
    //getForEntity : 객체 + header정보

    @Test
    public void show() {
        String location = createResource("/api/questions", createQuestion());
        softly.assertThat(template().getForEntity(location, Question.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update() {
        String location = createResource("/api/questions", updateQuestion());

        ResponseEntity<Question> responseEntity
                = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion()), Question.class);

        log.debug("response : {}", responseEntity.getBody());
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().getTitle()).isEqualTo("업데이트 제목");
    }

    @Test
    public void update_other_user() {
        String location = createResource("/api/questions", updateQuestion());

        ResponseEntity<Question> responseEntity
                = basicAuthTemplate(findByUserId("sanjigi")).exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion()), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_no_login() {
        String location = createResource("/api/questions", createQuestion());

        ResponseEntity<Question> responseEntity
                = template().exchange(location, HttpMethod.PUT, createHttpEntity(updateQuestion()), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_question_with_self_answer() {
        String location = createResource("/api/questions", createQuestion());
        Answer answer = new Answer("댓글입니다.");

        ResponseEntity<Question> answerCreateResponse = basicAuthTemplate()
                .postForEntity(location + "/answers", answer.getContents(), Question.class);

        ResponseEntity<Question> responseEntity
                = basicAuthTemplate().exchange(location, HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(responseEntity.getBody().isDeleted()).isTrue();
    }

    @Test
    public void delete_question_with_other_user_answer() {
        String location = createResource("/api/questions", createQuestion());                           // 질문 생성

        ResponseEntity<Question> answerCreateResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .postForEntity(location + "/answers", "댓글입니다.", Question.class);            // 위 질문에 댓글 달음

        ResponseEntity<Question> responseEntity
                = basicAuthTemplate().exchange(location, HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_question_other_user() {
        String location = createResource("/api/questions", createQuestion());
        Answer answer = new Answer("댓글입니다.");

        ResponseEntity<Question> answerCreateResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .postForEntity(location + "/answers", answer.getContents(), Question.class);

        ResponseEntity<Answer> responseEntity
                = basicAuthTemplate().exchange(location, HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Answer.class);

        log.debug("response : {}", responseEntity.getBody().getDeleted());
//        responseEntity.getBody().getAnswers().forEach(System.out::println);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_non_exist_question() {
        ResponseEntity<Void> responseEntity
                = basicAuthTemplate().exchange("/api/questions/999", HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
