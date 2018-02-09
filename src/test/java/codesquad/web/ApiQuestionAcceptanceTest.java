package codesquad.web;

import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() {
        QuestionDto questionDto = createQuestion().toQuestionDto();
        ResponseEntity<String> response = createResource(defaultUser(), questionDto, Question.class);
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(questionDto));
    }

    @Test
    public void create_로그인_없이() {
        QuestionDto questionDto = createQuestion().toQuestionDto();
        ResponseEntity<String> response = template()
                .postForEntity(getApiPath(Question.class), questionDto, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void read_누구나_가능() {
        ResponseEntity<String> response = createResource(defaultUser(), createQuestion().toQuestionDto(), Question.class);
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto dbQuestion = template()
                .getForObject(location, QuestionDto.class);
        assertNotNull(dbQuestion);
    }

    @Test
    public void update() {
        ResponseEntity<String> response = createResource(defaultUser(), createQuestion().toQuestionDto(), Question.class);
        String location = response.getHeaders().getLocation().getPath();

        QuestionDto questionDto = new QuestionDto("updateTitle", "updatedContents");
        basicAuthTemplate()
                .put(location, questionDto);

        QuestionDto dbQuestion = basicAuthTemplate()
                .getForObject(location, QuestionDto.class);

        assertThat(questionDto, is(dbQuestion));
    }

    @Test
    public void update_다른_사람() {
        ResponseEntity<String> response = createResource(defaultUser(), createQuestion().toQuestionDto(), Question.class);
        String location = response.getHeaders().getLocation().getPath();

        HttpHeaders headers = HtmlFormDataBuilder.getHeadersBy(MediaType.APPLICATION_JSON);
        QuestionDto questionDto = new QuestionDto("title", "updateContents");
        HttpEntity<QuestionDto> requestUpdate = new HttpEntity<>(questionDto, headers);

        response = basicAuthTemplate(differentUser())
                .exchange(location, HttpMethod.PUT, requestUpdate, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private Question createQuestion() {
        return new Question("title", "contents");
    }
}
