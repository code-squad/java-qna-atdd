package codesquad.web;

import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import support.test.AcceptanceTest;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create() throws Exception {
        QuestionDto questionDto = new QuestionDto("new title", "new contents");
        String location = createResource("/api/questions", questionDto);

        Question respQuestion = getResource(location, Question.class);
        assertThat(respQuestion.toQuestionDto(), is(questionDto));
    }

    @Test
    public void create_validation() throws Exception {
        QuestionDto questionDto = new QuestionDto("ne", "ne");
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/api/questions", questionDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        log.debug("validation error : {}", response.getBody());
    }

    @Test
    public void update() throws Exception {
        String location = createResource("/api/questions", new QuestionDto("new title", "new contents"));

        QuestionDto updatedQuestionDto = new QuestionDto("updated title", "updated contents");
        put(location, updatedQuestionDto);

        Question respQuestion = getResource(location, Question.class);
        assertThat(respQuestion.toQuestionDto(), is(updatedQuestionDto));
    }

    @Test
    public void delete() throws Exception {
        String location = createResource("/api/questions", new QuestionDto("new title", "new contents"));

        delete(location);
        Question respQuestion = getResource(location, Question.class);
        assertTrue(respQuestion.isDeleted());
    }
}
