package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import codesquad.service.QnaService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.utils.HtmlFormDataBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joeylee on 2018-01-27.
 */
public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    QnaService qnaService;

    @Test
    public void 질문_다_가져오기() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/", String.class);
        assertTrue(response.getBody().contains("runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?"));
        assertTrue(response.getBody().contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"));

    }

    @Test
    public void 로그인_하고_특정_질문_가져오기() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/1", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("국내에서 Ruby on Rails와 Play가 활성화되기 힘든 이유는 뭘까?"));
    }

    @Test
    public void 로그인_안하고_작성폼_가기() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문_작성() {
        HtmlFormDataBuilder htmlFormDataBuilder = urlEncodedForm().addParameter("title", "타이틀").addParameter("contents", "콘텐츠");
        ResponseEntity<String> response = basicAuthTemplate().postForEntity("/questions", htmlFormDataBuilder.build(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/"));
    }

    @Test
    public void 작성자가_자신글_수정() {
        HtmlFormDataBuilder htmlFormDataBuilder = urlEncodedForm().addParameter("title", "타이틀").addParameter("contents", "콘텐츠수정");
        ResponseEntity<String> response = put("/questions/1", htmlFormDataBuilder.build());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
     public void 특정질문_업데이트_폼_가기() throws Exception {
            ResponseEntity<String> response = basicAuthTemplate()
                                .getForEntity("/questions/1/form", String.class);

            assertThat(response.getStatusCode(), is(HttpStatus.OK));
            assertTrue(response.getBody().contains("수정"));
    }

    @Test
     public void 기본유저가_질문_지우기() throws Exception {
        ResponseEntity<String> response = delete("/questions/2");

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));

        response = basicAuthTemplate()
                           .getForEntity("/", String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("runtime 에 reflect 발동 주체 객체가 뭔지 알 방법이 있을까요?"));
   }

    @Test
    public void 작성자가_업데이트_폼_접근() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate()
                      .getForEntity("/questions/1/form", String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("수정"));
    }

}
