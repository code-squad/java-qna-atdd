package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest{

    @Test
    public void create() {
        AnswerDto answerDto = createAnswer(defaultUser());
        ResponseEntity<String> response = createResource(defaultUser(), answerDto, Answer.class);
        String location = response.getHeaders().getLocation().getPath();

        AnswerDto dbAnswerDto = template().getForObject(location, AnswerDto.class);
        assertThat(dbAnswerDto, is(answerDto));
    }

    @Test
    public void update() {
        ResponseEntity<String> response = createResource(defaultUser(), createAnswer(defaultUser()), Answer.class);
        String location = response.getHeaders().getLocation().getPath();
        AnswerDto dbAnswerDto = template().getForObject(location, AnswerDto.class);

        AnswerDto answerDto = new AnswerDto("updatedContents", dbAnswerDto.getQuestionId());
        basicAuthTemplate()
                .put(location, answerDto);

        AnswerDto dbAnswer = basicAuthTemplate()
                .getForObject(location, AnswerDto.class);

        assertThat(answerDto, is(dbAnswer));
    }

    @Test
    public void update_다른_사람() {
        ResponseEntity<String> response = createResource(defaultUser(), createAnswer(defaultUser()), Answer.class);
        String location = response.getHeaders().getLocation().getPath();
        AnswerDto dbAnswerDto = template().getForObject(location, AnswerDto.class);

        HttpHeaders headers = HtmlFormDataBuilder.getHeadersBy(MediaType.APPLICATION_JSON);
        AnswerDto answerDto = new AnswerDto("updatedContents", dbAnswerDto.getQuestionId());
        HttpEntity<AnswerDto> requestUpdate = new HttpEntity<>(answerDto, headers);

        response = basicAuthTemplate(differentUser())
                .exchange(location, HttpMethod.PUT, requestUpdate, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private AnswerDto createAnswer(User loginUser) {
        QuestionDto questionDto = createQuestion().toQuestionDto();
        ResponseEntity<String> response = createResource(loginUser, questionDto, Question.class);
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        return new AnswerDto("answerDefault", dbQuestion.getId());
    }

    private Question createQuestion() {
        return new Question("title", "contents");
    }
}
