package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.ArrayList;
import java.util.List;

import static codesquad.domain.AnswerTest.ANSWER;
import static codesquad.domain.QuestionTest.QUESTION;
import static codesquad.domain.QuestionTest.UPDATED_QUESTION;
import static codesquad.domain.UserTest.JUNGHYUN;
import static org.slf4j.LoggerFactory.getLogger;

public class ApiQuestionControllerTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiQuestionControllerTest.class);
    public static final String WRONG_QUESTION_ID = "100";

    @Test
    public void create() {
        Question newQuestion = new Question("테스트 질문1", "테스트 내용1");
        String location = createResource("/api/questions", newQuestion);
        Question dbQuestion = template().getForObject(location, Question.class);
        softly.assertThat(dbQuestion).isNotNull();
        log.info("dbQuestion : " + dbQuestion);
    }

    @Test
    public void create_no_login() {
        Question newQuestion = new Question("테스트 질문2", "테스트 내용2");
        ResponseEntity<String> response = template().postForEntity("/api/questions", newQuestion, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("errorMessage : {}", response.getBody());
    }

    @Test
    public void update() {
        Question newQuestion = new Question("테스트 질문3", "테스트 내용3");
        String location = createResource("/api/questions", newQuestion);
        Question original = template().getForObject(location, Question.class);

        ResponseEntity<Question> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Question.class);

        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(UPDATED_QUESTION.equalsTitleAndContents(responseEntity.getBody())).isTrue();
    }

    @Test
    public void update_no_login() {
        Question newQuestion = new Question("테스트 질문4", "테스트 내용4");
        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<Void> responseEntity = template().exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_other_user() {
        Question newQuestion = new Question("테스트 질문5", "테스트 내용5");
        String location = createResource("/api/questions", newQuestion);

        ResponseEntity<Void> responseEntity = basicAuthTemplate(JUNGHYUN).exchange(location, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_질문이_없을떄() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange("/api/questions/" + WRONG_QUESTION_ID, HttpMethod.PUT, createHttpEntity(UPDATED_QUESTION), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void delete_성공() {
        // 질문 생성
        Question newQuestion = new Question("테스트 질문6", "테스트 내용6");
        String location = createResource("/api/questions", newQuestion);
        Question dbQuestion = template().getForObject(location, Question.class);

        // 답변 생성
        List<String> answerLocations = gernerateAnswers(dbQuestion);

        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 답변 삭제 확인
        for (String answerLocation : answerLocations) {
            Answer answer = template().getForObject(answerLocation, Answer.class);
            softly.assertThat(answer.isDeleted()).isTrue();
        }
    }

    @Test
    public void delete_실패_다른유저답변_존재() {
        // 질문 생성
        Question newQuestion = new Question("테스트 질문7", "테스트 내용7");
        String location = createResource("/api/questions", newQuestion);
        Question dbQuestion = template().getForObject(location, Question.class);

        // 답변 생성
        List<String> answerLocations = gernerateAnswers(dbQuestion);

        // 다른 유저 답변 생성
        ResponseEntity<Void> answerResponseEntity = basicAuthTemplate(JUNGHYUN).postForEntity("/api/questions/" + dbQuestion.getId() + "/answers", ANSWER.getContents(), Void.class);
        answerLocations.add(answerResponseEntity.getHeaders().getLocation().getPath());

        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        // 답변 삭제 확인 - 롤백 체크
        for (String answerLocation : answerLocations) {
            Answer answer = template().getForObject(answerLocation, Answer.class);
            softly.assertThat(answer.isDeleted()).isFalse();
        }
    }

    @Test
    public void delete_성공_답변없음() {
        // 질문 생성
        Question newQuestion = new Question("테스트 질문8", "테스트 내용8");
        String location = createResource("/api/questions", newQuestion);
        Question dbQuestion = template().getForObject(location, Question.class);

        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange(location, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void delete_질문이_없을떄() {
        ResponseEntity<Void> responseEntity = basicAuthTemplate().exchange("/api/questions/" + WRONG_QUESTION_ID, HttpMethod.DELETE, createHttpEntity(null), Void.class);
        softly.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private List<String> gernerateAnswers(Question dbQuestion) {
        List<String> answerLocations = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String answerLocation = createResource("/api/questions/" + dbQuestion.getId() + "/answers", ANSWER.getContents());
            answerLocations.add(answerLocation);
            Answer answer = template().getForObject(answerLocation, Answer.class);
            softly.assertThat(answer.isDeleted()).isFalse();
        }
        return answerLocations;
    }
}
