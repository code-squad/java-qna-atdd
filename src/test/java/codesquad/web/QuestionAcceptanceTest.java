package codesquad.web;

import codesquad.HtmlFormDataBuilder;
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
import support.test.AcceptanceTest;

//테스트 실행 순서 정렬 가능, but 테스트 간 의존성을 검증할 겸 특별한 경우가 아닌 이상 하지 않는 게 좋을 듯?
public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "test title")
                .addParameter("contents", "test contents")
                .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByTitle("test title").isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaultUser().getName());
    }

    @Test
    public void updateForm_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains("국내에서 Ruby on Rails");
    }

    @Test
    public void updateForm_no_login() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 1),
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update() throws Exception {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/1");
    }

    @Test
    public void update_no_login() throws Exception {
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "updated title")
                .addParameter("contents", "updated contents")
                .build();
        return template.postForEntity(String.format("/questions/%d", 1), request, String.class);
    }

    //TODO : Acceptance tests for deletion
//    @Test
//    public void delete() throws Exception {
//        ResponseEntity<String> response = delete(basicAuthTemplate());
//        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
//        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/1");
//    }
//
//    @Test
//    public void delete_no_login() throws Exception {
//        ResponseEntity<String> response = delete(template());
//        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
//        log.debug("body : {}", response.getBody());
//    }
//
//    private ResponseEntity<String> delete(TestRestTemplate template) throws Exception {
//        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
//                .addParameter("_method", "put")
//                .addParameter("title", "updated title")
//                .addParameter("contents", "updated contents")
//                .build();
//        return template.postForEntity(String.format("/questions/%d", 1), request, String.class);
//    }
}
