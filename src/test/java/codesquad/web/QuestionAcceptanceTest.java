package codesquad.web;

import codesquad.HtmlFormDataBuilder;
import codesquad.domain.QuestionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static codesquad.domain.UserTest.SANJIGI;


public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LogManager.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    QuestionRepository questionRepository;

    @Test
    public void createFormOk() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createFormFailed() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void create() throws Exception {
        String title = "나는야 peter";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", "test dydydy")
                .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", request, String.class);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByTitle(title).isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void listTest() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaltQuestion().getTitle());

    }

    @Test
    public void showFormOkTest() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaltQuestion().getContents());
    }


    @Test
    public void updateFormNoTest() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/1/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void updateFormOkTest() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/1/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = getUpdateResponse(basicAuthTemplate());

        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith(defaltQuestion().generateUrl());
    }

    @Test
    public void update_no() throws Exception {
        ResponseEntity<String> response = getUpdateResponse(template());

        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    private ResponseEntity<String> getUpdateResponse(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder
                .urlEncodedForm()
                .put()
                .addParameter("title", "하하하")
                .addParameter("contents", "test dydydy")
                .build();
        return template.postForEntity(defaltQuestion().generateUrl(), request, String.class);
    }


    @Test
    public void deleted() throws Exception {
        ResponseEntity<String> response = getStringResponseEntity(basicAuthTemplate());
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");

    }

    @Test
    public void deleted_no() throws Exception {
        ResponseEntity<String> response = getStringResponseEntity(template());
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void deleted_no_other_answer() throws Exception {
        ResponseEntity<String> response = getStringResponseEntity(basicAuthTemplate(SANJIGI));
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

    }

    private ResponseEntity<String> getStringResponseEntity(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm().delete().build();
        return template.postForEntity(defaltQuestion().generateUrl(), request, String.class);
    }
}
