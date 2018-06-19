package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
    private static final String QUESTION_URL = "/api/qna";
    private QuestionDto newQuestion;

    @Before
    public void init() {
        newQuestion = makeTestQuestionDto();
    }

    @Test
    public void create() throws Exception {
        String location = createResource(QUESTION_URL, newQuestion);
        QuestionDto dbQuestion = getResoure(location, QuestionDto.class, defaultUser());
        assertTrue(dbQuestion.equalsTitleAndContent(newQuestion));
    }

    @Test
    public void create_guest() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(User.GUEST_USER).postForEntity(QUESTION_URL, newQuestion, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update() throws Exception {
        String location = createResource(QUESTION_URL, newQuestion);

        QuestionDto updateQuestion = new QuestionDto(newQuestion.getId(), "aaaaa", "sssss");
        basicAuthTemplate().put(location, updateQuestion);

        QuestionDto dbQuestion = getResoure(location, QuestionDto.class, defaultUser());
        assertTrue(dbQuestion.equalsTitleAndContent(updateQuestion));
    }

    @Test
    public void update_guest() throws Exception {
        String location = createResource(QUESTION_URL, newQuestion);

        QuestionDto updateQuestion = new QuestionDto(newQuestion.getId(), "aaaaa", "sssss");
        basicAuthTemplate(User.GUEST_USER).put(location, updateQuestion);

        QuestionDto dbQuestion = getResoure(location, QuestionDto.class, defaultUser());

        // 바뀌었나 안바뀌었나 테스트
        assertTrue(dbQuestion.equalsTitleAndContent(newQuestion));
    }

    @Test
    public void delete() {
        String location = createResource(QUESTION_URL, newQuestion);

        basicAuthTemplate().delete(location);
    }
}
