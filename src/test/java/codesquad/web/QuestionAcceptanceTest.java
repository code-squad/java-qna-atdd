package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static testhelper.HtmlFormDataBuilder.urlEncodedForm;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;


public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Before
    public void init() {

    }

    @Test
    public void accessFormWithoutLogin() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void accessFormWithLogin() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/form", String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void postTest() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request;
        request = urlEncodedForm().addParameter("title", "test")
                .addParameter("contents", "contests")
                .build();
        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/questions/"));
    }

    @Test
    public void postTest_invalid_input() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request;
        request = urlEncodedForm().addParameter("title", "")
                .addParameter("contents", "contests")
                .build();
        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity("/questions", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/qna/form"));
    }

    @Test
    public void getPostTest() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/1", String.class);

        assertTrue(response.getBody().contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"));
    }

    @Test
    public void getPostTest_invalid_postNo() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/100", String.class);

        assertTrue(response.getBody().contains("질문하기"));
    }

    @Test
    public void accessUpdateForm_with_other_user() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/2/form", String.class);

        assertFalse(response.getBody().contains("수정하기"));
    }

    @Test
    public void accessUpdateForm_with_writer() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/questions/1/form", String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("수정하기"));
    }

    @Test
    public void updateQuestionTest_with_writer() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request;
        request = urlEncodedForm().addParameter("title", "title")
                .addParameter("contents", "수정되었다")
                .build();

        ResponseEntity<String> response = put("/questions/1", request);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("수정되었다"));
    }

    @Test
    public void updateQuestionTest_with_other_user() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request;
        request = urlEncodedForm().addParameter("title", "title")
                .addParameter("contents", "수정되었다")
                .build();

        ResponseEntity<String> response = put("/questions/2", request);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void deleteQuestionTest_with_owner() throws Exception {
        ResponseEntity<String> response = delete("/questions/1");

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));

        response = basicAuthTemplate()
                .getForEntity("/", String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertFalse(response.getBody().contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"));
    }

    @Test
    public void deleteQuestionTest_with_other_user() throws Exception {
        ResponseEntity<String> response = delete("/questions/2");

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));

        response = basicAuthTemplate()
                .getForEntity("/", String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?"));
    }

    @Test
    public void questionListTest() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity("/", String.class);

        assertTrue(response.getBody().contains("runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?"));
        assertTrue(response.getBody().contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"));
    }
}
