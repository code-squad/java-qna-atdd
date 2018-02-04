package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository repository;

    @Test
    public void questionsForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    @DirtiesContext
    public void create() throws Exception {
        QuestionDto questionDto = new QuestionDto("test", "test context");

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions",
                                                                                         htmlRequest(questionDto),
                                                                                         String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(repository.findByWriter(defaultUser()).size(), is(3));
    }

    @Test
    public void create_잘못된질문형식_TITLE이3보다작은경우() throws Exception {
        QuestionDto questionDto = new QuestionDto("t", "test");

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions",
                                                                                         htmlRequest(questionDto),
                                                                                         String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void show() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void show_존재하지않는질문() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/10", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void updateForm() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity("/questions/1/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void updateForm_권한이없는사용자() throws Exception {
        User user = new User("sanjigi", "test", "산지기", "sanjigi@slipp.net");

        ResponseEntity<String> response = basicAuthTemplate(user).getForEntity("/questions/1/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm_존재하지않는질문() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity("/questions/10/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    @DirtiesContext
    public void update() throws Exception {
        QuestionDto questionDto = new QuestionDto("update", "update test");
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange("/questions/1",
                                                                                    HttpMethod.PUT,
                                                                                    htmlRequest(questionDto),
                                                                                    String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        Question question = repository.findOne(1L);

        assertThat(question.getTitle(), is("update"));
        assertThat(question.getContents(), is("update test"));
    }

    @Test
    public void update_권한이없는사용자() throws Exception {
        User user = new User("sanjigi", "test", "산지기", "sanjigi@slipp.net");
        QuestionDto questionDto = new QuestionDto("update", "update test");
        ResponseEntity<String> response = basicAuthTemplate(user).exchange("/questions/1",
                                                                           HttpMethod.PUT,
                                                                           htmlRequest(questionDto),
                                                                           String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    @DirtiesContext
    public void delete() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange("/questions/4", HttpMethod.DELETE,
                                                                                    HttpEntity.EMPTY, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        Question question = repository.findOne(4L);
        assertThat(question.isDeleted(), is(true));
    }

    @Test
    public void delete_답변에다른답변자가존재하는경우() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).exchange("/questions/1", HttpMethod.DELETE,
                                                                                    HttpEntity.EMPTY, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_권한이없는사용자() throws Exception {
        User user = new User("sanjigi", "test", "산지기", "sanjigi@slipp.net");
        ResponseEntity<String> response = basicAuthTemplate(user).exchange("/questions/1", HttpMethod.DELETE,
                                                                           HttpEntity.EMPTY, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }
}
