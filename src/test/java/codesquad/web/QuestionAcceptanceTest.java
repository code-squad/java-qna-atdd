package codesquad.web;

import codesquad.HtmlFormDataBuilder;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.slf4j.LoggerFactory.getLogger;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void createFormWithoutLogin() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void createFormWithLogin() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "questionTestTitle")
                .addParameter("contents", "testContents")
                .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByTitle("questionTestTitle").isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());

        softly.assertThat(response.getBody()).contains(questionRepository.findById((long)2).get().getTitle());
    }

    @Test
    public void showQuestionWithoutLogin() {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(defaultQuestion().getContents());
    }

    @Test
    public void showQuestionWithLogin() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/2", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(questionRepository.findById((long)2).get().getContents());
    }

    @Test
    public void showUpdateForm() {
        ResponseEntity<String> response = basicAuthTemplate(sanjigiUser()).getForEntity("/questions/2/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getBody()).contains(questionRepository.findById((long)2).get().getContents());
    }

    @Test
    public void showUpdateFormWithInValidUser() {
        ResponseEntity<String> response = basicAuthTemplate(sanjigiUser()).getForEntity("/questions/1/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void showUpdateFormWithoutLogin() {
        ResponseEntity<String> response = template().getForEntity("/questions/1/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void update() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.put()
                .addParameter("title", "modifyTestTitle")
                .addParameter("contents", "modifyTestContents")
                .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByTitle("modifyTestTitle").isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void updateWithInValidUser() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.put()
                .addParameter("title", "modifyTestTitle")
                .addParameter("contents", "modifyTestContents")
                .build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions/2", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        softly.assertThat(questionRepository.findByTitle("runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?', '설계를 희한하게 하는 바람에 꼬인 문제같긴 합니다만. 여쭙습니다. 상황은 mybatis select 실행될 시에 return object 의 getter 가 호출되면서인데요. getter 안에 다른 property 에 의존중인 코드가 삽입되어 있어서, 만약 다른 mybatis select 구문에 해당 property 가 없다면 exception 이 발생하게 됩니다.").isPresent()).isFalse();
    }

    @Test
    public void updateWithoutLogin() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.put()
                .addParameter("title", "modifyTestTitle")
                .addParameter("contents", "modifyTestContents")
                .build();

        ResponseEntity<String> response = template().postForEntity("/questions/2", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.delete().build();
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/4", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByTitle("deleteTest").get().isDeleted()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void deleteWithoutLogin() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.delete().build();
        ResponseEntity<String> response = template().postForEntity("/questions/3", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(questionRepository.findByTitle("testTitle").get().isDeleted()).isFalse();
    }

    @Test
    public void deleteWithInvalidLogin() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.delete().build();
        ResponseEntity<String> response = basicAuthTemplate(sanjigiUser()).postForEntity("/questions/3", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        softly.assertThat(questionRepository.findByTitle("testTitle").get().isDeleted()).isFalse();
    }


}

