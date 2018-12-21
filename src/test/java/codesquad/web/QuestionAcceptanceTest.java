package codesquad.web;

import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void createForm_no_login() throws Exception {
        ResponseEntity<String> response = template()
                .getForEntity("/questions", String.class); //입력값
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createForm_login() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void create() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("title", "제목입니다.")
                .addParameter("contents", "내용입니다.")
                .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions",request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void updateForm_no_login() {
        //template : 로그인한 안한 상태 , basicAuthTemplate() : 로그인함
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void updateForm_other_user() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", 2), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void updateForm_self() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity(String.format("/questions/%d/form", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void update_no_login() {
        HttpEntity<MultiValueMap<String, Object>> request = modifyQuestion();
        ResponseEntity<String> response = template().postForEntity(String.format("/questions/%d", 1), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void update_other_user() {
        HttpEntity<MultiValueMap<String, Object>> request = modifyQuestion();
        ResponseEntity<String> response = basicAuthTemplate().postForEntity(String.format("/questions/%d", 2), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void update_self() {
        HttpEntity<MultiValueMap<String, Object>> request = modifyQuestion();
        ResponseEntity<String> response = basicAuthTemplate().postForEntity(String.format("/questions/%d", 1),request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    public HttpEntity<MultiValueMap<String, Object>> modifyQuestion() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "제목입니다.")
                .addParameter("contents", "내용입니다.")
                .build();
    }


    @Test
    public void delete_no_login() {
        HttpEntity<MultiValueMap<String, Object>> request = deleteQuestion();
        ResponseEntity<String> response = template().postForEntity(String.format("/questions/%d", 2), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete_other_user() {
        HttpEntity<MultiValueMap<String, Object>> request = deleteQuestion();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(String.format("/questions/%d", 2), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void delete_self() {
        HttpEntity<MultiValueMap<String, Object>> request = deleteQuestion();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(String.format("/questions/%d", 1), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    public HttpEntity<MultiValueMap<String, Object>> deleteQuestion() {
        return HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();
    }

}
