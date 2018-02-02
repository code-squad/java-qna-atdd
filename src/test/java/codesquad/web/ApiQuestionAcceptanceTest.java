package codesquad.web;

import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.http.*;
import support.test.AcceptanceTest;


import static codesquad.dto.QuestionDto.apiUrl;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    public static final String URL_BASE = "/api/questions";
    public static final QuestionDto NEW_QUESTION = new QuestionDto(4, "question", "contents");
    public static final QuestionDto UPDATED_QUESTION = new QuestionDto(1, "updated", "updated");
    public static final String SANJIGI = "sanjigi";

    @Test
    public void create() throws Exception {
        String location = create(URL_BASE, NEW_QUESTION);

        QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
        assertThat(dbQuestion, is(NEW_QUESTION));
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = put(apiUrl(1L), UPDATED_QUESTION);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        QuestionDto dbQuestion = template().getForObject(apiUrl(1L), QuestionDto.class);
        assertThat(dbQuestion, is(UPDATED_QUESTION));
    }

    @Test
    public void update_by_not_owner() throws Exception {
        ResponseEntity<String> response = put(apiUrl(2L), UPDATED_QUESTION);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));

        QuestionDto dbQuestion = template().getForObject(apiUrl(2L), QuestionDto.class);
        assertThat(dbQuestion, not(UPDATED_QUESTION));
    }

    @Test
    public void delete() throws Exception {
        ResponseEntity<String> response = delete(SANJIGI, apiUrl(2L));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        QuestionDto dbQuestion = template().getForObject(apiUrl(2L), QuestionDto.class);
        assertThat(dbQuestion, is(nullValue()));
    }

    @Test
    public void delete_not_exist_question() throws Exception {
        ResponseEntity<String> response = delete(apiUrl(0L));
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void delete_owner_answer_also_owner() throws Exception {
        ResponseEntity<String> response = delete(SANJIGI, apiUrl(3L));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        QuestionDto dbQuestion = template().getForObject(apiUrl(3L), QuestionDto.class);
        assertThat(dbQuestion, is(nullValue()));
    }

    @Test
    public void delete_owner_answer_not_owner() throws Exception {
        ResponseEntity<String> response = delete(apiUrl(1L));
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

}
