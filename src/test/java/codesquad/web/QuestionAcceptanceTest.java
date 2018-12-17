package codesquad.web;

import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.slf4j.LoggerFactory.getLogger;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger logger = getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 로그인한유저_게시글작성() {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인안한유저_게시글작성() {
        ResponseEntity<String> response = template()
                .getForEntity("/questions", String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        logger.debug("body : {}", response.getBody());
    }

    @Test
    public void 게시글작성() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodingForm()
                .addParameter("title", "제목1")
                .addParameter("contents", "컨텐츠1")
                .build();

        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity("/questions", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(questionRepository.findByTitle("제목1").isPresent()).isTrue();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void 게시글읽기() {
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/%d", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인안했을때_게시글수정안됨() {
        ResponseEntity<String> response = template()
                .getForEntity(String.format("/questions/%d/form", defaultUser().getId()), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문자와_로그인유저가_다를때_게시글수정안됨() {
        User user = new User();
        ResponseEntity<String> response = update(basicAuthTemplate(user));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문자와_로그인유저가_같을때_게시글수정() {
        ResponseEntity<String> response = update(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
    }

    private ResponseEntity<String> update(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodingForm()
                .put()
                .addParameter("title", "타이틀2")
                .addParameter("contents", "내용2")
                .build();

        return template.postForEntity(String.format("/questions/%d", 1), request, String.class);
    }

    @Test
    public void 로그인안한유저_게시글삭제안됨() {
        ResponseEntity<String> response = delete(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문자와_로그인유저가_같을때_게시글삭제가능() {
        ResponseEntity<String> response = delete(basicAuthTemplate());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void 질문자와_로그인유저가_다를때_게시글삭제안됨() {
        User loginUser = new User();
        ResponseEntity<String> response = delete(basicAuthTemplate(loginUser));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<String> delete(TestRestTemplate template) {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodingForm()
                .delete()
                .build();
        return template.postForEntity(String.format("/questions/%d", 1), request, String.class);
    }

}
