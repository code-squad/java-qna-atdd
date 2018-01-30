package codesquad.web;

import codesquad.HtmlFormDataBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Test
    public void 질문_등록_폼() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 질문_등록() throws Exception {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("title", "new title")
                .addParameter("contents", "new contents")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
    }

    @Test
    public void 질문_상세보기() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/", 1), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 질문_삭제() throws Exception {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = basicAuthTemplate().exchange(String.format("/questions/%d/", 1), HttpMethod.DELETE, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void 질문_수정_폼() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/1/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 질문_수정() throws Exception {
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("title", "update title")
                .addParameter("contents", "update contents")
                .build();
        ResponseEntity<String> response = basicAuthTemplate().exchange(String.format("/questions/%d/", 1), HttpMethod.PUT, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
        log.debug("body : {}", response.getBody());
    }
}
