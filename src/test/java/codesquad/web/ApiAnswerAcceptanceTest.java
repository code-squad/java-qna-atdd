package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
    private QuestionDto createQuestionDto(String title) {
        return new QuestionDto(title, "contents");
    }

    private AnswerDto createAnswerDto(QuestionDto question) {
        return createAnswerDto("test answer", question);
    }

    private AnswerDto createAnswerDto(String contents, QuestionDto question) {
        return new AnswerDto()
                .setWriter(defaultUser())
                .setContents(contents)
                .setQuestion(new Question()
                        .setTitle(question.getTitle())
                        .setContents(question.getContents()));

    }

    @Test
    public void create() throws Exception {
        QuestionDto questionDto = createQuestionDto("test2");
        String location = createResourceDefaultLogin("/api/questions", questionDto);
        QuestionDto dbQuestion = getResource(location, QuestionDto.class);

        AnswerDto answerDto = createAnswerDto(dbQuestion);
        location = createResourceDefaultLogin("/api/questions/" + dbQuestion.getId() +"/answers", answerDto);
        AnswerDto dbAnswer = getResource(location, AnswerDto.class);

        assertThat(dbAnswer, is(answerDto));
    }

    @Test
    public void showQuestionDetail() throws Exception {
        QuestionDto questionDto = createQuestionDto("test2");
        String location = createResourceDefaultLogin("/api/questions", questionDto);
        QuestionDto dbQuestion = getResource(location, QuestionDto.class);

        AnswerDto answerDto = createAnswerDto(dbQuestion);
        location = createResourceDefaultLogin("/api/questions/" + dbQuestion.getId() +"/answers", answerDto);
        ResponseEntity<String> response = template().getForEntity(location, String.class);

        assertThat(response.getBody().contains("answer"), is(true));
    }

    @Test
    public void delete() throws Exception {
        QuestionDto questionDto = createQuestionDto("test2");
        String location = createResourceDefaultLogin("/api/questions", questionDto);
        QuestionDto dbQuestion = getResource(location, QuestionDto.class);

        AnswerDto answerDto = createAnswerDto(dbQuestion);
        location = createResourceDefaultLogin("/api/questions/" + dbQuestion.getId() +"/answers", answerDto);
        AnswerDto dbAnswer = getResource(location, AnswerDto.class);
        log.debug("{}", dbAnswer);

        basicAuthTemplate().delete(location, dbAnswer);
        dbAnswer = getResource(location, AnswerDto.class);
        log.debug("{}", dbAnswer);

        assertNull(dbAnswer);
    }

    @Test
    public void delete_no_authority() throws Exception {
        QuestionDto questionDto = createQuestionDto("test2");
        String location = createResourceDefaultLogin("/api/questions", questionDto);
        QuestionDto dbQuestion = getResource(location, QuestionDto.class);

        AnswerDto answerDto = createAnswerDto(dbQuestion);
        location = createResourceDefaultLogin("/api/questions/" + dbQuestion.getId() +"/answers", answerDto);
        AnswerDto dbAnswer = getResource(location, AnswerDto.class);

        User user = new User("sehwan", "test", "sehwan", "sehwan@woowa.com");
        basicAuthTemplate(user).delete(location, dbAnswer);
        dbAnswer = getResource(location, AnswerDto.class);

        assertNotNull(dbAnswer);
    }
}
