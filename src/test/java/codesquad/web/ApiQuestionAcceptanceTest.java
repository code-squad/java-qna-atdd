package codesquad.web;

import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final String CREATE_URL = "/api/questions/create";
    private static final String DEFAULT_QUESTION_1_URL = "/api/questions/1";
    private static final Logger logger = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create_logged_in() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        ResponseEntity<QuestionDto> response = basicAuthTemplate().postForEntity(CREATE_URL, questionDto, QuestionDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));

        assertThat(response.getBody().getTitle(), is(questionDto.getTitle()));
        assertThat(response.getBody().getContents(), is(questionDto.getContents()));
    }

    @Test
    public void create_NOT_logged_in() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        ResponseEntity<QuestionDto> response = template().postForEntity(CREATE_URL, questionDto, QuestionDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void show() {
        ResponseEntity<QuestionDto> response = template().getForEntity(DEFAULT_QUESTION_1_URL, QuestionDto.class);
        assertThat(response.getStatusCode(), is(HttpStatus.ACCEPTED));

        logger.debug("Question Object: {}", response.getBody().getContents());
    }

    @Test
    public void update_logged_in() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        String location = createResource(CREATE_URL, questionDto, QuestionDto.class);

        QuestionDto update = new QuestionDto("title", "updated content");
        basicAuthTemplate().put(location, update);

        QuestionDto original = getResource(location, QuestionDto.class);
        assertThat(original.getContents(), is(update.getContents()));
    }

    @Test
    public void update_NOT_logged_in() {
        QuestionDto questionDto = new QuestionDto("title", "content");
        String location = createResource(CREATE_URL, questionDto, QuestionDto.class);

        QuestionDto update = new QuestionDto("title", "updated content");
        template().put(location, update);

        QuestionDto original = getResource(location, QuestionDto.class);
        assertNotEquals(original.getContents(), update.getContents());
    }
}