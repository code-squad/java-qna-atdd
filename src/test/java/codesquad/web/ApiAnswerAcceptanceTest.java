package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Test
    public void createAnswer() {
        User loginUser =defaultUser();
        AnswerDto answerDto = new AnswerDto(loginUser, "새로운 답변입니다");

        String location = createAuthResource(standardAnswerUrl(), answerDto);
        AnswerDto dbAnswer = getResource(location, AnswerDto.class);

        assertThat(dbAnswer, is(dbAnswer));
    }

    @Test
    public void updateAnswer() {
        User loginUser = defaultUser();
        AnswerDto answerDto = new AnswerDto(loginUser, "새로운 답변입니다.");
        String location = createAuthResource(standardAnswerUrl(), answerDto);

        AnswerDto updateAnswer = new AnswerDto(loginUser, "수정된 답변입니다");
        basicAuthTemplate(loginUser).put(location, updateAnswer);

        AnswerDto insertAnswer = getResource(location, AnswerDto.class);

        assertThat(insertAnswer, is(updateAnswer));

    }

    @Test
    public void update_다른사람() {
        User anotherUser = findByUserId("sanjigi");
        User loginUser = defaultUser();
        AnswerDto answerDto = new AnswerDto(loginUser, "새로운 답변입니다.");
        String location = createAuthResource(standardAnswerUrl(), answerDto);

        AnswerDto updateAnswer = new AnswerDto(loginUser, "수정된 답변입니다");

        basicAuthTemplate(anotherUser).put(location, updateAnswer);

        AnswerDto insertAnswer = getResource(location, AnswerDto.class);
        assertThat(insertAnswer, is(answerDto));
    }


    @Test
    public void deleteAnswer() {
        User loginUser = defaultUser();
        AnswerDto answerDto = new AnswerDto(loginUser, "새로운 답변입니다.");
        String location = createAuthResource(standardAnswerUrl(), answerDto);

        basicAuthTemplate(loginUser).delete(location);
        ResponseEntity<String> response = template().getForEntity(location, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.GONE));
    }

    @Test
    public void delete_다른사람() {
        User anotherUser = findByUserId("sanjigi");
        User loginUser = defaultUser();
        AnswerDto answerDto = new AnswerDto(loginUser, "새로운 답변입니다.");
        String location = createAuthResource(standardAnswerUrl(), answerDto);

        basicAuthTemplate(anotherUser).delete(location);

        AnswerDto answer = getResource(location, AnswerDto.class);
        assertThat(answer, is(answerDto));
    }
}
