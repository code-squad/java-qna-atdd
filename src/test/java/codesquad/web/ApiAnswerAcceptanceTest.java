package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.UserTest;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void create() {
        String location = createResource("/api/questions", updateQuestion());

        Answer newAnswer = new Answer(UserTest.SANJIGI, "댓글입니다.");
        ResponseEntity<Question> answerResponse = basicAuthTemplate(findByUserId("sanjigi"))
                .postForEntity(location + "/answers", newAnswer.getContents(), Question.class);
        log.debug("answerResponse: {}" + answerResponse);

        softly.assertThat(answerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}
