package codesquad.web;

import codesquad.dto.AnswerDto;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
    private static final String URL_BASE = "/api/questions/1/answers";
    private static final AnswerDto NEW_ANSWER = new AnswerDto(4,"Test Answer");
    private static final AnswerDto UPDATED_ANSWER = new AnswerDto(1,"Updated Answer");

    @Test
    public void create() throws Exception {
        String location = create(URL_BASE, NEW_ANSWER);

        AnswerDto dbAnswer = template().getForObject(location, AnswerDto.class);
        assertThat(dbAnswer, is(NEW_ANSWER));
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = put(URL_BASE + "/" + 1, UPDATED_ANSWER);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        AnswerDto dbAnswer = template().getForObject(URL_BASE + "/" + 1, AnswerDto.class);
        assertThat(dbAnswer, is(UPDATED_ANSWER));
    }

    @Test
    public void update_with_other() throws Exception {
        ResponseEntity<String> response = put(URL_BASE + "/" + 2, UPDATED_ANSWER);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete() throws Exception {
        ResponseEntity<String> response = delete(URL_BASE + "/" + 1);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        response = template().getForEntity(URL_BASE + "/" + 1, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void delete_with_other() throws Exception {
        ResponseEntity<String> response = delete(URL_BASE + "/" + 2);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }
}
