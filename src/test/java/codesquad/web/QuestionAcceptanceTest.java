package codesquad.web;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;


import static org.slf4j.LoggerFactory.getLogger;

public class QuestionAcceptanceTest extends AcceptanceTest {
    
    private static final Logger logger = getLogger(QuestionAcceptanceTest.class);

    private TestRestTemplate testRestTemplate;

    @Before
    public void setUp() {
        testRestTemplate = template().withBasicAuth("javajigi", "test");
    }

    @Test
    public void 질문하기페이지이동_로그인X()  {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문하기페이지이동_로그인O()  {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/questions/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody().contains("질문하기")).isTrue();
    }

    @Test
    public void 질문하기_로그인X() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.createHtmlFormDataBuilder()
                                                                .addParameter("writer_id", "javajigi")
                                                                .addParameter("title", "Test Question By javajigi")
                                                                .addParameter("contents", "Test Contents By javajigi")
                                                                .build();

        ResponseEntity<String> response = template().postForEntity("/api/questions", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void 질문하기_로그인O_글자수3미만() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.createHtmlFormDataBuilder()
                .addParameter("writer_id", "javajigi")
                .addParameter("title", "T")
                .addParameter("contents", "C")
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/questions", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        logger.debug("Body : " + response.getBody());
    }

    @Test
    public void 질문하기_로그인O_글자수3이상() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.createHtmlFormDataBuilder()
                                                                .addParameter("writer_id", "javajigi")
                                                                .addParameter("title", "Test Question By javajigi")
                                                                .addParameter("contents", "Test Contents By javajigi")
                                                                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/questions", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getHeaders().getLocation().getPath().equals("/")).isTrue();
    }

    @Test
    public void 질문하기_삭제_로그인O() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.createHtmlFormDataBuilder()
                                                                .deleteParameter().build();
        ResponseEntity<String> response = testRestTemplate.postForEntity("/questions/1", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath().equals("/")).isTrue();
    }

    @Test
    public void 질문목록() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.debug("body : " + response.getBody());
    }

    @Test
    public void 질문상세보기_로그인O() {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.debug("질문상세보기_로그인O body : " + response.getBody());
    }

    @Test
    public void 질문하기_수정_로그인O() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.createHtmlFormDataBuilder()
                                                                .addParameter("title", "Modified Title")
                                                                .addParameter("contents", "Modified Contents")
                                                                .addParameter("id", "1")
                                                                .putParameter().build();
        ResponseEntity<String> response = testRestTemplate.postForEntity("/questions", request, String.class);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/questions/1");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    public void 질문하기_수정_이동_로그인O() {
        ResponseEntity<String> response = testRestTemplate.getForEntity("/questions/1/form", String.class);
        logger.debug("질문하기_수정_이동_로그인O body : " + response.getBody());
    }

    @Test
    public void 질문하기_수정_이동_로그인X() {
        ResponseEntity<String> response = template().getForEntity("/questions/1/form", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}