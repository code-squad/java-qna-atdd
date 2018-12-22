package codesquad.web;

import codesquad.domain.Answer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void create() {
        Answer answer = new Answer(defaultUser(), "test contents");
        ResponseEntity<String> answerResponse = basicAuthTemplate().postForEntity("/api/questions/1/answers", answer, String.class);
        
        Answer dbAnswer = getResource(answerResponse.getHeaders().getLocation().getPath(), Answer.class, defaultUser());
        softly.assertThat(dbAnswer).isNotNull();
    }
}
