package codesquad.web;

import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final String QUESTION_PATH = "/api/questions";

    private QuestionDto question;

    @Before
    public void setUp() throws Exception {
        question = new QuestionDto("title", "contents");
    }

    @Test
    public void write() throws Exception {
        String resource = createResource(QUESTION_PATH, question);
        QuestionDto dbQuestion = getResource(resource, QuestionDto.class, defaultUser());
        assertThat(dbQuestion).isEqualTo(question);
    }

    @Test
    public void update() throws Exception {
        String resource = createResource(QUESTION_PATH, question);

        QuestionDto update = new QuestionDto("update title", "update contents");
        basicAuthTemplate(defaultUser()).put(resource, update);

        QuestionDto dbQuestion = getResource(resource, QuestionDto.class, defaultUser());
        assertThat(dbQuestion).isEqualTo(update);
    }

    @Test
    public void update_권한없음() {
        String resource = createResource(QUESTION_PATH, question);

        QuestionDto update = new QuestionDto("update title", "update contents");
        basicAuthTemplate(findByUserId("sanjigi")).put(resource, update);

        assertThat(getResource(resource, QuestionDto.class, defaultUser())).isEqualTo(question);
    }

    @Test
    public void delete() {
        String resource = createResource(QUESTION_PATH, question);

        basicAuthTemplate(defaultUser()).delete(resource, String.class);
        assertThat(getResource(resource, QuestionDto.class, defaultUser()).isDeleted()).isTrue();
    }

    @Test
    public void delete_권한없음() {
        String resource = createResource(QUESTION_PATH, question);

        basicAuthTemplate(findByUserId("sanjigi")).delete(resource, String.class);
        assertThat(getResource(resource, QuestionDto.class, defaultUser()).isDeleted()).isFalse();

    }

    private ResponseEntity<String> writeQuestion(QuestionDto question) {
        return basicAuthTemplate().postForEntity("/api/questions", question, String.class);
    }

    private QuestionDto getQuestion(ResponseEntity<String> response) {
        return template().getForObject(getLocation(response), QuestionDto.class);
    }

    private String getLocation(ResponseEntity<String> response) {
        return response.getHeaders().getLocation().getPath();
    }



}