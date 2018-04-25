package codesquad.web;

import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.helper.HtmlFormDataBuilder;
import support.test.AcceptanceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 질문_생성폼() {
        ResponseEntity<String> responseEntity = template().getForEntity("/questions/form", String.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", responseEntity.getBody());
    }

    @Test
    public void 질문_생성() throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
        builder.addParameter("title", "title test")
                .addParameter("contents", "contents test");

        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", builder.build(), String.class);

        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.FOUND));
        assertNotNull(questionRepository.findOne(1L));
        assertThat(response.getHeaders().getLocation().getPath(), CoreMatchers.is("/questions"));
    }

    @Test
    public void 질문_목록() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 로그인하지_않고_수정폼_접근() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 1L), String.class);
        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 로그인하고_수정폼_접근() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", 1L), String.class);
        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.OK));
    }

    @Test
    public void 질문_수정() throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder
                .urlEncodedForm()
                .put();

        builder.addParameter("title", "modify test")
                .addParameter("content", "modify content");

        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity(String.format("/questions/%d", 1L), builder.build(), String.class);
        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/questions"));
    }

    @Test
    public void 질문_삭제() throws Exception {
        HtmlFormDataBuilder builder = HtmlFormDataBuilder
                .urlEncodedForm()
                .delete();

        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity(String.format("/questions/%d", 1L), builder.build(), String.class);
        assertThat(response.getStatusCode(), CoreMatchers.is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/questions"));
    }
}
