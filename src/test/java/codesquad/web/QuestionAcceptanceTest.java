package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    private HtmlFormDataBuilder htmlFormDataBuilde;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm_no_login() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        logger.debug("body : {}", response.getBody());
    }

    @Test
    public void createForm_login() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        htmlFormDataBuilde = HtmlFormDataBuilder.urlEncodedForm();

        User user = defaultUser();
        htmlFormDataBuilde.addParameter("title", "test");
        htmlFormDataBuilde.addParameter("contents", "contents");

        ResponseEntity<String> response = basicAuthTemplate(user).postForEntity("/questions", htmlFormDataBuilde.build(), String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByWriterId(user.getId())).isNotEmpty();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaultUser().getName());
    }

    @Test
    public void updateForm_no_login() {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 2),
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_not_match_user_id() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", 2),
                String.class);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(String.format("/questions/%d", 2));
    }

    @Test
    public void updateForm_login() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", 1),
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        logger.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws UnAuthorizedException {
        htmlFormDataBuilde = HtmlFormDataBuilder.urlEncodedForm().put();

        htmlFormDataBuilde.addParameter("title", "title2");
        htmlFormDataBuilde.addParameter("contents", "contents2");

        return template.postForEntity(String.format("/questions/%d", 1), htmlFormDataBuilde.build(), String.class);
    }

    @Test
    public void update_not_match_user_id() {
        ResponseEntity<String> response = update(basicAuthTemplate(secondUser()));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        logger.debug("body : {}", response.getBody());
    }

    @Test
    public void update_onwer() {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        logger.debug("html : {}", response.getBody());
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(String.format("/questions/%d", 1));
    }

    @Test
    public void delete_no_login() {
        ResponseEntity<String> response = delete(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<String> delete(TestRestTemplate template) {
        htmlFormDataBuilde = HtmlFormDataBuilder.urlEncodedForm().delete();

        return template.postForEntity(String.format("/questions/%d", 1), htmlFormDataBuilde.build(), String.class);
    }

    @Test
    public void delete_not_match_user_id() {
        ResponseEntity<String> response = delete(basicAuthTemplate(secondUser()));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(String.format("/questions/%d", 1));
    }

    @Test
    public void delete() {
        ResponseEntity<String> response = delete(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }
}
