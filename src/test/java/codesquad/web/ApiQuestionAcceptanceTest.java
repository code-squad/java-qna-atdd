package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

import codesquad.domain.Question;
import support.test.BasicAuthAcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class ApiQuestionAcceptanceTest extends BasicAuthAcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Test
    public void create_entity() throws Exception {
        Question question = new Question("TDD는 의미있는 활동인가?", "당근 엄청 의미있는 활동이고 말고..");
        ResponseEntity<String> response = basicAuthTemplate.postForEntity("/api/questions", question, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertNotNull(response.getHeaders().get("location"));
    }

    @Test
    public void create_invalid() throws Exception {
        Question question = new Question("T", "T");
        ResponseEntity<String> response = basicAuthTemplate.postForEntity("/api/questions", question, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        log.debug("body : {}", response.getBody());
    }

    private String createQuestion(User writer) {
        QuestionDto dto = new QuestionDto("title", "contents");
        return createResource("/api/questions", dto, writer);
    }

    private ResponseEntity<Void> deleteQuestion(String redirectPath, User loginUser) {
        return basicAuthTemplate(loginUser)
                .exchange(redirectPath,
                        HttpMethod.DELETE,
                        HtmlFormDataBuilder.json().build(),
                        Void.class);
    }
}
