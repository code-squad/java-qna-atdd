package codesquad.web;

import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.helper.HtmlFormDataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class QnaAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void create() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", "new contents")
                .addParameter("title", "new title")
                .build();
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", request, String.class);

        log.debug("response : {}", response.toString());

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findAll().stream()
                .filter((x) -> x.getContents().equals("new contents") && x.getTitle().equals("new title"))
                .count())
                .isEqualTo(1);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void create_로그인_안함() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("contents", "contents")
                .addParameter("title", "title")
                .build();
        ResponseEntity<String> response = template().postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void list() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).contains(defaultQuestion().getTitle());
        softly.assertThat(response.getBody()).contains(findByQuestionId(2l).getTitle());
    }

    @Test
    public void show() {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody())
                .contains(defaultQuestion().getContents())
                .contains(defaultQuestion().getTitle());
    }

    @Test
    public void update() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "updated title")
                .addParameter("contents", "updated contents")
                .build();
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findById(1l).get().getTitle()).isEqualTo("updated title");
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/1");
    }

    @Test
    public void update_로그인_안함() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "updated title")
                .addParameter("contents", "updated contents")
                .build();
        ResponseEntity<String> response = template().postForEntity("/questions/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void delete() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
        softly.assertThat(questionRepository.findById(1l).get().isDeleted()).isTrue();
    }

    @Test
    public void delete_다른_유저(){
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();

        ResponseEntity<String> response = basicAuthTemplate(new User(2, "otherId", "pwd", "name", "m@il"))
                .postForEntity("/questions/1", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        softly.assertThat(questionRepository.findById(1l).get().isDeleted()).isFalse();
    }
}