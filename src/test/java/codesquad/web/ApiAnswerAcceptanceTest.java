package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

    @Test
    public void create() throws Exception {
        String generateUrl = createQuestion();
        AnswerDto answerDto = new AnswerDto("new answer");
        String location = createResource(generateUrl + "/answers", answerDto);

        Answer respAnswer = getResource(location, Answer.class);
        assertThat(respAnswer.toAnswerDto(), is(answerDto));
    }

    @Test
    public void create_validation() throws Exception {
        String generateUrl = createQuestion();
        AnswerDto answerDto = new AnswerDto("ne");

        ResponseEntity<String> response = basicAuthTemplate().postForEntity(generateUrl+ "/answers", answerDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        log.debug("validation error : {}", response.getBody());
    }

    @Test
    public void update() throws Exception {
        String generateUrl = createQuestion();
        AnswerDto answerDto = new AnswerDto("new answer");
        String location = createResource(generateUrl + "/answers", answerDto);

        AnswerDto updatedAnswerDto = new AnswerDto("updated answer");
        basicAuthTemplate().put(location, updatedAnswerDto);

        Answer respAnswer = getResource(location, Answer.class);
        assertThat(respAnswer.toAnswerDto(), is(updatedAnswerDto));
    }

    @Test
    public void delete() throws Exception {
        String generateUrl = createQuestion();
        AnswerDto answerDto = new AnswerDto("new answer");
        String location = createResource(generateUrl + "/answers", answerDto);

        basicAuthTemplate().delete(location);
        Answer respAnswer = getResource(location, Answer.class);
        assertTrue(respAnswer.isDeleted());
    }

    private String createQuestion() {
        QuestionDto questionDto = new QuestionDto("title", "contents");
        String location = createResource("/api/questions", questionDto);

        Question respQuestion = getResource(location, Question.class);
        assertThat(respQuestion.toQuestionDto(), is(questionDto));
        return respQuestion.generateRestUrl();
    }
}
