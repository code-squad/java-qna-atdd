package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.util.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptance extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptance.class);
    private static final User LEARNER = new User(1L, "learner", "9229", "TAEWON", "htw@gmail.com");

    private long getIdfromLocation(String location) {
        return Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
    }

    @Test
    public void create() {
        Question question = new Question("The dip, dip은 무엇인가요?", "dip을 지나가는 중인가요?");
        String comment = "지금은 dip을 버텨야 하는 기간입니다.";

        String location = createResource("/api/questions", question, LEARNER);
        Long questionId = getIdfromLocation(location);

        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("comment", comment);
        ResponseEntity<String> response = basicAuthTemplate(LEARNER).postForEntity("/api/questions/" + questionId + "/answers/", builder.build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void update() {
        Question question = new Question("실행이 답인가요?", "계획 -> 실행 -> 피드백 루프가 맞지 않나요?");
        String location = createResource("/api/questions", question, LEARNER);
        Long questionId = getIdfromLocation(location);

        Answer answer = new Answer(LEARNER, "책, 실행이 답이다를 참고하세요.");
        location = createResource("/api/questions/" + questionId + "/answers/", answer, LEARNER);
        log.debug("location : {}", location);

        String updateComment = "책, 지치지 않는 힘을 참고하세요.";
        Answer updateAnswer = new Answer(LEARNER, updateComment);

        basicAuthTemplate(LEARNER).put(location, updateAnswer);
        Answer dbAnswer = getResource(location, Answer.class);

        assertThat(updateAnswer.getContents(), is(dbAnswer.getContents()));
    }
}
