package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiAnswerAcceptanceTest.class);

    protected Question createQuestion() {
        return new Question("제목입니다.", "내용입니다.");
    }

    @Test
    public void create_글자수_제한() {
        String location = createResource("/api/questions", createQuestion());
        Answer newAnswer = new Answer("댓");

        ResponseEntity<Question> answerResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .postForEntity(location + "/answers", newAnswer.getContents(), Question.class);
        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void create_no_login() {
        String location = createResource("/api/questions", createQuestion());
        Answer newAnswer = new Answer("a");

        ResponseEntity<Question> answerResponse = template().postForEntity(location + "/answers", newAnswer.getContents(), Question.class);     // 로그인 체크를 글자수 체크 보다 더 먼저한다.
        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void delete() {
        String location = createResource("/api/questions", createQuestion());       // 질문을 만들었음 , 반환값 : /api/questions/{만들질문ID}
        Answer newAnswer = new Answer("댓글입니다.");          // sanjigi가 만든 댓글

        ResponseEntity<Question> answerCreateResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .postForEntity(location + "/answers", newAnswer.getContents(), Question.class);       //sanjigi가 위에 만든 질문안에 위에서 만든 댓글을 달음 , apiAnswerController에서 질문 생성한 맵핑메서드의 반환값이 나오므로 제네릭이 Question, Answer을 넣어도 controller의 반환값인 question이 반환된다.
        log.debug("answerCreateResponse : {}", answerCreateResponse);                                            // /api/questions/{만들질문ID}/answer로 보낸 응답값

        ResponseEntity<Question> answerDeleteResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(location + "/answers/" + answerCreateResponse.getBody().getId(), HttpMethod.DELETE, createHttpEntity(answerCreateResponse.getBody()), Question.class);
        // sanjigi가 위와 같은 상황에서 아까 만든 댓글을 지웠다. , (주의 할점은 exchange 의 매개변수에서 HttpEntity부에는 location에 맵핑한 메서드의 매개변수와 일치하게 된다. -- 여기선 맵핑할 메서드의 매개변수가 없으므로 아무거나 와도 상관 없다.)
        // answerId 와 questionId가 우연한 일치로 3번으로 똑같다. answerId를 가져오려면 answer를 생성할때 내용으로만 생성할때는 하드코딩 or answer를 생성하려고 할때 id값을 부여해야 한다.
        log.debug("answerDeleteResponse : {}", answerDeleteResponse);
        softly.assertThat(answerDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(answerDeleteResponse.getBody().isDeleted()).isTrue();
    }

    @Test
    public void delete_no_login() {
        String location = createResource("/api/questions", createQuestion());
        Answer newAnswer = new Answer("댓글입니다.");

        ResponseEntity<Question> answerCreateResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .postForEntity(location + "/answers", newAnswer.getContents(), Question.class);

        ResponseEntity<Answer> answerDeleteResponse = template()
                .exchange(location + "/answers/" + answerCreateResponse.getBody().getId(), HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Answer.class);

        softly.assertThat(answerDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_other_answer() {
        String location = createResource("/api/questions", createQuestion());
        Answer newAnswer = new Answer("댓글입니다.");

        ResponseEntity<Question> answerCreateResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .postForEntity(location + "/answers", newAnswer.getContents(), Question.class);

        ResponseEntity<Answer> answerDeleteResponse = basicAuthTemplate()
                .exchange(location + "/answers/" + answerCreateResponse.getBody().getId(), HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), Answer.class);

        softly.assertThat(answerDeleteResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
