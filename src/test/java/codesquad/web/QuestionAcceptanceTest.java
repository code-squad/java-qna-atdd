package codesquad.web;

import codesquad.converter.HtmlFormDataBuilder;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.util.logging.LogManager;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log =  LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void create() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        log.info("status code : {}", response.getStatusCode());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void makeQuestion() throws Exception {
        User loginUser = defaultUser();
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm()
                .addParams("title", "제목1111")
                .addParams("contents", "내용1111");
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", htmlFormDataBuilder.build(), String.class);
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void list() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void showQnA() throws Exception {
        int id = 1;
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", id), String.class);
        log.info("body : {}", response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void updateQuestion() throws Exception {
        User loginUser = defaultUser();
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm()
                .addParams("_method", "put")
                .addParams("title", "제목수정성공")
                .addParams("contents", "내용수정성공");
        Question question = questionRepository.getOne(loginUser.getId());
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d", question.getId()), htmlFormDataBuilder.build(), String.class);
        assertThat(response.getStatusCode().value(), is(302));
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
    }

    @Test
    public void deleteQuestion() throws Exception {
        User loginUser = defaultUser();
        int id = 1;
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm()
                .addParams("_method", "delete");
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d", id), htmlFormDataBuilder.build(), String.class);
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
    }
}
