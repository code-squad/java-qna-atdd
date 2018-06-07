package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create_no_login() {
        QuestionDto questionDto = createDto();
        ResponseEntity<String> response = template().postForEntity("/api/questions", questionDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void create_login() {
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(newQuestion, is(dbQuestion));
    }

    @Test
    public void update_login() {
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        QuestionDto updateQuestion = new QuestionDto("titleTest2", "contentsTest2");
        basicAuthTemplate().put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
        assertThat(updateQuestion, is(dbQuestion));
    }

    @Test
    public void update_다른_사용자() {
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        QuestionDto updateQuestion = new QuestionDto("titleTest2", "contentsTest2");
        basicAuthTemplate(findByUserId("riverway")).put(location, updateQuestion);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, findByUserId("riverway"));
        assertThat(newQuestion, is(dbQuestion));
    }

    @Test
    public void delete_login() {
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        basicAuthTemplate().delete(location);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
        log.debug("dbQuestion : {}", dbQuestion);
        assertNull(dbQuestion);
    }

    @Test
    public void delete_다른_사용자() {
        QuestionDto newQuestion = createDto();

        String location = createResource("/api/questions", newQuestion, basicAuthTemplate());
        basicAuthTemplate(findByUserId("riverway")).delete(location);

        QuestionDto dbQuestion = getResource(location, QuestionDto.class, findByUserId("riverway"));
        assertThat(dbQuestion, is(newQuestion));
    }

    @Test
    public void delete_자신의_답변만_존재() {
        QuestionDto newQuestion = createDto();

        String questionsLocation = createResource("/api/questions", newQuestion, basicAuthTemplate());

        String newContents = "testContents1";
        String answerLocation = createResource(questionsLocation + "/answers", newContents, basicAuthTemplate());

        basicAuthTemplate(defaultUser()).delete(questionsLocation);

        QuestionDto dbQuestion = getResource(questionsLocation, QuestionDto.class, defaultUser());
        Answer dbAnswer = getResource(answerLocation, Answer.class, defaultUser());
        assertNull(dbQuestion);
        assertNull(dbAnswer);
    }

    @Test
    public void delete_다른사람의_답변_존재() {
        QuestionDto newQuestion = createDto();

        String questionsLocation = createResource("/api/questions", newQuestion, basicAuthTemplate());

        String newContents = "testContents2";
        String answerLocation = createResource(questionsLocation + "/answers", newContents, basicAuthTemplate(findByUserId("riverway")));

        basicAuthTemplate(defaultUser()).delete(questionsLocation);

        QuestionDto dbQuestion = getResource(questionsLocation, QuestionDto.class, defaultUser());
        Answer dbAnswer = getResource(answerLocation, Answer.class, defaultUser());
        assertThat(dbQuestion, is(newQuestion));
        assertThat(dbAnswer.getContents(), is(newContents));
    }

    @Test
    public void delete_자신과_다른사람의_답변_존재() {
        QuestionDto newQuestion = createDto();

        String questionsLocation = createResource("/api/questions", newQuestion, basicAuthTemplate());

        String newContents = "testContents3";
        String answerLocation = createResource(questionsLocation + "/answers", newContents, basicAuthTemplate());
        String newContentsOfOther = "testContents4";
        String answerLocationOfOther = createResource(questionsLocation + "/answers", newContentsOfOther, basicAuthTemplate(findByUserId("riverway")));

        basicAuthTemplate(defaultUser()).delete(questionsLocation);

        QuestionDto dbQuestion = getResource(questionsLocation, QuestionDto.class, defaultUser());
        Answer dbAnswer = getResource(answerLocation, Answer.class, defaultUser());
        log.debug("Answer : {}", dbAnswer);
        Answer dbAnswerOfOther = getResource(answerLocationOfOther, Answer.class, defaultUser());
        log.debug("Answer2 : {}", dbAnswerOfOther);

        assertThat(dbQuestion, is(newQuestion));
        assertThat(dbAnswer.getContents(), is(newContents));
        assertThat(dbAnswerOfOther.getContents(), is(newContentsOfOther));
    }

    private QuestionDto createDto() {
        return new QuestionDto("titleTest", "contentsTest");
    }
}
