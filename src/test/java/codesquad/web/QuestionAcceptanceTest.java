package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();

        HttpEntity<MultiValueMap<String, Object>> request = builder
                .addParameter("title", "title")
                .addParameter("contents", "contents")
                .build();

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
    }

    @Test
    public void list() throws Exception {
        // ResponseEntity<String> response =template().getForEntity("/qna", String.class);
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void updateForm_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 1),
                String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void update_no_login() throws Exception {
        template().put("/questions/1", getMultiValueMapHttpEntity());
        Question target = questionRepository.findOne(1l);

        assertNotEquals(target.getTitle(), "title2");
        assertNotEquals(target.getContents(), "contents");
    }

    @Test
    public void update() throws Exception {
        basicAuthTemplate().put("/questions/1", getMultiValueMapHttpEntity());
        Question target = questionRepository.findOne(1l);

        assertThat(target.getTitle(), is("title2"));
        assertThat(target.getContents(), is("contents"));
    }

    private HttpEntity<MultiValueMap<String, Object>> getMultiValueMapHttpEntity() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "title2")
                .addParameter("contents", "contents")
                .build();
    }

    private HttpEntity<MultiValueMap<String, Object>> update(TestRestTemplate template) throws Exception {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "title2")
                .addParameter("contents", "contents")
                .build();
    }

    @Test
    public void can_not_delete() throws CannotDeleteException {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/3/delete", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.INTERNAL_SERVER_ERROR)); // CannotDeleteException을 잡아내지 못함.
    }

    @Test
    public void delete() {
        basicAuthTemplate().delete("/questions/1");
        Question question = questionRepository.findOne(1l);

        assertThat(question.isDeleted(), is(true));
    }
}
