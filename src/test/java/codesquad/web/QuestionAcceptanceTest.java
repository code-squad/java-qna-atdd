package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;
import support.test.Method;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Before
    public void clearData() {
        deleteAllQuestions();
    }

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create_no_login() throws Exception {
        ResponseEntity<String> response = create(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void create_login() throws Exception {
        ResponseEntity<String> response = create(basicAuthTemplate(defaultUser()));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
    }

    public ResponseEntity<String> create(TestRestTemplate template) throws Exception {
        return htmlFormDataBuilderTemplate.executePostForEntity(template, "/questions", Method.POST,
                (HtmlFormDataBuilder htmlFormDataBuilder) -> {

                    htmlFormDataBuilder.addParameter("title", "title");
                    htmlFormDataBuilder.addParameter("contents", "contents");
                    return htmlFormDataBuilder.build();
                });
    }

    @Test
    public void show() {
        Question question = createQuestion(defaultUser());

        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", question.getId()),
                String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(question.getContents()), is(true));
    }

    @Test
    public void updateForm_no_login() throws Exception {
        Question question = createQuestion(defaultUser());

        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", question.getId()),
                String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateForm_login() throws Exception {
        User loginUser = defaultUser();
        Question question = createQuestion(loginUser);

        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity(String.format("/questions/%d/form", question.getId()),
                String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(question.getContents()), is(true));
    }

    @Test
    public void update_no_login() throws Exception {
        Question question = createQuestion(defaultUser());

        ResponseEntity<String> response = update(template(), question);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void update_login() throws Exception {
        User loginUser = defaultUser();
        Question question = createQuestion(loginUser);

        ResponseEntity<String> response = update(basicAuthTemplate(loginUser), question);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/questions"));
    }

    private ResponseEntity<String> update(TestRestTemplate template, Question question) throws Exception {
        String targetUrl = String.format("/questions/%d", question.getId());
        return htmlFormDataBuilderTemplate.executePostForEntity(template, targetUrl, Method.PUT,
                (HtmlFormDataBuilder htmlFormDataBuilder) -> {

                    htmlFormDataBuilder.addParameter("title", "title");
                    htmlFormDataBuilder.addParameter("contents", "contents");
                    return htmlFormDataBuilder.build();
                });
    }

    @Test
    public void delete_no_login() throws Exception {
        Question question = createQuestion(defaultUser());

        ResponseEntity<String> response = delete(template(), question);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void delete_login() throws Exception {
        User loginUser = defaultUser();
        Question question = createQuestion(loginUser);

        ResponseEntity<String> response = delete(basicAuthTemplate(), question);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
    }

    public ResponseEntity<String> delete(TestRestTemplate template, Question question) throws Exception {
        String targetUrl = String.format("/questions/%d", question.getId());
        return htmlFormDataBuilderTemplate.executePostForEntity(template, targetUrl, Method.DELETE,
                (HtmlFormDataBuilder htmlFormDataBuilder) -> htmlFormDataBuilder.build());
    }

}
