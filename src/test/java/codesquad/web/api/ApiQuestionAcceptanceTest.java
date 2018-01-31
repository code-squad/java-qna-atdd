package codesquad.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import codesquad.domain.ContentType;
import codesquad.domain.DeleteHistory;
import codesquad.domain.DeleteHistoryRepository;
import codesquad.domain.DeletedId;
import codesquad.domain.Question;
import codesquad.domain.QuestionList;
import codesquad.dto.AnswerDto;
import codesquad.dto.HateoasResponse;
import codesquad.dto.QuestionDto;
import support.test.AcceptanceTest;

import static codesquad.domain.ContentType.ANSWER;
import static codesquad.domain.ContentType.QUESTION;
import static codesquad.utils.HtmlFormDataBuilder.jsonEncodedForm;
import static codesquad.utils.StringUtils.getIdBySubLocation;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    @Test
    public void 질문의_get요청이_정상적인가() {
        ResponseEntity<String> response = template().getForEntity("/api/questions/1", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void 질문_리스트의_get요청이_정상적인가() throws IOException {
        ResponseEntity<String> response = template().getForEntity("/api/questions", String.class);
        HateoasResponse<QuestionList> questionResponse = new ObjectMapper().readValue(response.getBody(), HateoasResponse.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(questionResponse.getEmbedded());
        assertNotNull(questionResponse.getLinks());
        assertNotNull(questionResponse.getPage());
    }

    @Test
    public void 질문_생성을_위한_post요청이_정상적인가() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        String location = createResource("/api/questions", questionDto);

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(questionDto.setId(dbQuestion.getId())));
    }

    @Test
    public void 질문_수정을_위한_put요청이_정상적인가() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        String location = createResource("/api/questions", questionDto);
        String putLocation = putResource(location, questionDto.setContents("수정했습니당"));

        QuestionDto dbQuestion = template().getForObject(putLocation, QuestionDto.class);
        assertThat(dbQuestion, is(questionDto.setId(dbQuestion.getId())));
    }

    @Test
    public void 질문_삭제를_위한_delete요청이_정상적인가() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        String location = createResource("/api/questions", questionDto);
        deleteResource(location);
    }

    @Test
    public void 자신의_질문에_답변자가_같으면_삭제가_가능한가() throws IOException {
        String questionLocation = createResource("/api/questions", new QuestionDto("title", "content"));
        createResource(questionLocation + "/answers", new AnswerDto("contents"));
        String deleteLocation = deleteResource(questionLocation);
        ResponseEntity<String> response = template().getForEntity(deleteLocation, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        Question questionResponse = new ObjectMapper().readValue(response.getBody(), Question.class);
        assertTrue(questionResponse.isDeleted());
    }

    @Test
    public void 자신의_질문이_아니면_삭제가_불가능한가() {
        String questionLocation = createResource("/api/questions", new QuestionDto("title", "content"));
        createResource(questionLocation + "/answers", new AnswerDto("contents"));
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).exchange(questionLocation, HttpMethod.DELETE, jsonEncodedForm().build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 자신의_질문에_답변자가_다르면_삭제가_불가능한가() {
        String questionLocation = createResource("/api/questions", new QuestionDto("title", "content"));
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi")).postForEntity(questionLocation + "/answers", new AnswerDto("contents"), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        ResponseEntity<String> deletedResponse = basicAuthTemplate(defaultUser()).exchange(questionLocation, HttpMethod.DELETE, jsonEncodedForm().build(), String.class);
        assertThat(deletedResponse.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문_삭제시_히스토리를_등록하는가() {
        String questionLocation = createResource("/api/questions", new QuestionDto("title", "content"));
        String answerLocation = createResource(questionLocation + "/answers", new AnswerDto("contents"));
        String deletedLocation = deleteResource(questionLocation);

        checkDeleteHistory(deletedLocation, QUESTION);
        checkDeleteHistory(answerLocation, ANSWER);
    }

    private void checkDeleteHistory(String deletedLocation, ContentType question) {
        DeleteHistory questionDeleteHistory = deleteHistoryRepository.findById(DeletedId.builder().contentId(getIdBySubLocation(deletedLocation)).contentType(question).build());
        assertNotNull(questionDeleteHistory);
    }
}
