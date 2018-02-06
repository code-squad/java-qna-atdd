package codesquad.web;

import codesquad.dto.AnswerDto;
import org.junit.Before;
import org.junit.Test;
import support.test.AcceptanceTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ApiAcceptanceAnswerTest extends AcceptanceTest {
    private static final String ANSWER_PATH = "/api/questions/1/answers";

    private AnswerDto answer;

    @Before
    public void setUp() throws Exception {
        answer = new AnswerDto("contents");
    }

    @Test
    public void write() throws Exception {
        String resource = createResource(ANSWER_PATH, answer);
        AnswerDto dbAnswer = getResource(resource, AnswerDto.class, defaultUser());
        assertThat(dbAnswer.getContents()).isEqualTo(answer.getContents());
    }

    @Test
    public void update() throws Exception {
        String resource = createResource(ANSWER_PATH, answer);

        AnswerDto update = new AnswerDto("updated contents");
        basicAuthTemplate(defaultUser()).put(resource, update);

        AnswerDto dbAnswer = getResource(resource, AnswerDto.class, defaultUser());
        assertThat(update.getContents()).isEqualTo(dbAnswer.getContents());

    }

    @Test
    public void update_권한없음() throws Exception {
        String resource = createResource(ANSWER_PATH, answer);

        AnswerDto update = new AnswerDto("updated contents");
        basicAuthTemplate(findByUserId("sanjigi")).put(resource, update);

        AnswerDto dbAnswer = getResource(resource, AnswerDto.class, defaultUser());
        assertThat(answer.getContents()).isEqualTo(dbAnswer.getContents());

    }

    @Test
    public void delete() {
        String resource = createResource(ANSWER_PATH, answer);

        basicAuthTemplate(defaultUser()).delete(resource, String.class);
        AnswerDto dbAnswer = getResource(resource, AnswerDto.class, defaultUser());
        assertThat(dbAnswer).isNull();

    }

    @Test
    public void delete_권한없음() {
        String resource = createResource(ANSWER_PATH, answer);

        basicAuthTemplate(findByUserId("sanjigi")).delete(resource, String.class);
        AnswerDto dbAnswer = getResource(resource, AnswerDto.class, defaultUser());
        assertThat(dbAnswer.isDeleted()).isFalse();

    }



}
