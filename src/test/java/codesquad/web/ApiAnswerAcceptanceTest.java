package codesquad.web;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;



public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Test
    public void create() {
        String answer = createNewAnswer();
        String location = createResourceUsingAuth(defaultUser(), "/api/questions/1/answers", answer);
        ResponseEntity<String> response = getResource(defaultUser(), location);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void update() {
        String answer = createNewAnswer();
        putResource(defaultUser(), "/api/questions/1/answers/1/form", answer);

        String updatedAnswer = getResource("/api/questions/1/answers/1/", String.class, defaultUser());
        assertThat(updatedAnswer, is(createNewAnswer()));
    }

    @Test
    public void delete() {
        deleteResource(defaultUser(), "/api/questions/1/answers/1");
        String updatedAnswer = getResource("/api/questions/1/answers/1/", String.class, defaultUser());
        assertThat(updatedAnswer).isNotNull();
    }

    private String createNewAnswer() {
        return "this is for test code";
    }
}
