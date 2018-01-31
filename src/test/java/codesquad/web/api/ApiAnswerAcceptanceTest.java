package codesquad.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import codesquad.domain.Answer;
import codesquad.domain.DeleteHistory;
import codesquad.domain.DeleteHistoryRepository;
import codesquad.domain.DeletedId;
import codesquad.dto.AnswerDto;
import support.test.AcceptanceTest;

import static codesquad.domain.ContentType.ANSWER;
import static codesquad.utils.StringUtils.getIdBySubLocation;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    @Test
    public void 답변의_get요청이_정상적인가() throws IOException {
        ResponseEntity<String> response = template().getForEntity("/api/questions/1/answers/1", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertNotNull(parsingAnswer(response));
    }

    @Test
    public void 답변_생성을_위한_post요청이_정상적인가() {
        AnswerDto answerDto = new AnswerDto(1L, "contents");
        String location = createResource("/api/questions/1/answers", answerDto);

        AnswerDto dbAnswerDto = template().getForObject(location, AnswerDto.class);
        assertThat(dbAnswerDto, is(answerDto.setQuestionId(dbAnswerDto.getQuestionId())));
    }

    @Test
    public void 답변_수정을_위한_put요청이_정상적인가() throws IOException {
        ResponseEntity<String> response = template().getForEntity("/api/questions/1/answers/1", String.class);
        Answer answer = parsingAnswer(response);
        AnswerDto answerDto = new AnswerDto(answer.getQuestion().getId(), "수정했습니당");
        String location = putResource("/api/questions/1/answers/1", answerDto);

        AnswerDto dbAnswerDto = template().getForObject(location, AnswerDto.class);
        assertThat(dbAnswerDto, is(answerDto.setQuestionId(dbAnswerDto.getQuestionId())));
    }

    @Test
    public void 답변_삭제를_위한_delete요청이_정상적인가() {
        AnswerDto answerDto = new AnswerDto(1L, "contents");
        String location = createResource("/api/questions/1/answers", answerDto);
        deleteResource(location);
    }

    @Test
    public void 답변_삭제시_히스토리를_등록하는가() {
        AnswerDto answerDto = new AnswerDto(1L, "contents");
        String location = createResource("/api/questions/1/answers", answerDto);
        String deletedLocation = deleteResource(location);

        DeleteHistory deleteHistory = deleteHistoryRepository.findById(DeletedId.builder().contentId(getIdBySubLocation(deletedLocation)).contentType(ANSWER).build());
        assertNotNull(deleteHistory);
    }

    private Answer parsingAnswer(ResponseEntity<String> answerResponse) throws IOException {
        return new ObjectMapper().readValue(answerResponse.getBody(), Answer.class);
    }
}
