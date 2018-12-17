package codesquad.web;

import codesquad.domain.User;
import codesquad.security.BasicAuthInterceptor;
import codesquad.service.QnaService;
import org.junit.Before;
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
    private static final Logger log = getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QnaService qnaService;

    private User loginUser;

    @Before
    public void setUp() {
        this.loginUser = defaultUser();
    }

    @Test
    public void 질문생성테스트_로그인안됨() {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문생성테스트_로그인됨() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions", String.class);
//        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions");
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 질문생성() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "please please")
                .addParameter("contents", "please")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(qnaService.findById(loginUser.getId())).isNotEmpty();
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void 업데이트_로그인안됨() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void 업데이트_로그인됨() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity("/questions/1", String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 질문수정테스트() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = update(basicAuthTemplate(loginUser));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/1");
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "수정 제발좀")
                .addParameter("contents", "수정 contents 되야합니다.")
                .build();
        return template.postForEntity(String.format("/questions/%d", 1), request, String.class);
    }

    @Test
    public void 질문삭제테스트_로그인안됨_아이디다를때() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/1", String.class);
        log.debug("body : {}", response.getBody());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 질문삭제테스트_로그인됨() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity("/questions/1", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

}
