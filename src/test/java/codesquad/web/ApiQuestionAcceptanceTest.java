package codesquad.web;

import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ApiQuestionAcceptanceTest extends AcceptanceTest{

    @Test
    public void create() {
        QuestionDto questionDto = createQuestionDto();
        String location = createResourceUsingAuth(defaultUser(),"/api/questions/", questionDto);
        ResponseEntity<String> response = getResource(defaultUser(), location);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void update() {
        QuestionDto questionDto = createQuestionDto();
        putResource(defaultUser(), "/api/questions/1", questionDto);

        QuestionDto updatedQuestion = getResource("/api/questions/1", QuestionDto.class, defaultUser());
        assertThat(updatedQuestion.getTitle(), is(questionDto.getTitle()));
        assertThat(updatedQuestion.getContents(), is(questionDto.getContents()));
    }

    @Test
    public void delete() {
        deleteResource(defaultUser(), "/api/questions/1");

        QuestionDto questionDto = getResource("/api/questions/1", QuestionDto.class, defaultUser());
        assertThat(questionDto).isNotNull();
    }

    private QuestionDto createQuestionDto() {
        return new QuestionDto(11, "test title", "test contents");
    }
}
