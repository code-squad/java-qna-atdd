package codesquad.web.api;

import codesquad.domain.QuestionList;
import codesquad.dto.HateoasResponse;
import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Test
    public void question의_get요청이_정상적인가() {
        ResponseEntity<String> response = template().getForEntity("/api/questions/1", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void question_리스트의_get요청이_정상적인가() throws IOException {
        ResponseEntity<String> response = template().getForEntity("/api/questions", String.class);
        HateoasResponse<QuestionList> questionResponse = new ObjectMapper().readValue(response.getBody(), HateoasResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(questionResponse.getEmbedded());
        assertNotNull(questionResponse.getLinks());
        assertNotNull(questionResponse.getPage());
    }

    @Test
    public void question_생성을_위한_post요청이_정상적인가() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        String location = createResource("/api/questions", questionDto);

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(questionDto.setId(dbQuestion.getId())));
    }

    @Test
    public void question_수정을_위한_put요청이_정상적인가() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        String location = createResource("/api/questions", questionDto);
        String putLocation = putResource(location, questionDto.setContents("수정했습니당"));

        QuestionDto dbQuestion = template().getForObject(putLocation, QuestionDto.class);
        assertThat(dbQuestion, is(questionDto.setId(dbQuestion.getId())));
    }

    @Test
    public void question_삭제를_위한_delete요청이_정상적인가() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        String location = createResource("/api/questions", questionDto);
        deleteResource(location);
    }
}
