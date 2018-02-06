package codesquad.web;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.helper.HtmlFormDataBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class QuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    QuestionRepository questionRepository;

    @Test
    public void write_success() {
        User loginUser = defaultUser();
        Question newQuestion = new Question("aaaa","aaa");
        HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("title", newQuestion.getTitle())
                .addParameter("contents", newQuestion.getContents())
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);

        Question question = questionRepository.findOne(3L);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertNotNull(question);
        assertThat(question.isContentsEquals(newQuestion), is(true));
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
    }

    @Test
    public void update_login() {
        User loginUser = defaultUser();

        HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("_method", "PUT")
                .addParameter("title", "bbbb")
                .addParameter("contents", "bbb")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", 1), request, String.class);

        Question question = questionRepository.findOne(1L);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));

        assertNotNull(question);
        assertThat(question.getTitle(), is("bbbb"));
        assertThat(question.getContents(), is("bbb"));
        assertThat(response.getHeaders().getLocation().getPath(), is("/"));
    }


    @Test
    public void update_no_login() throws UnAuthorizedException {
        User loginUser = defaultUser();

        HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("_method", "PUT")
                .addParameter("title", "bbbb")
                .addParameter("contents", "bbb")
                .build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", 2), request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
    }

    @Test
    public void delete() {
        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request
                = HtmlFormDataBuilder
                .urlEncodedForm()
                .addParameter("_method", "delete")
                .build();

        ResponseEntity<String> response
                = basicAuthTemplate(loginUser)
                .postForEntity(String.format("/questions/%d", 1), request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(questionRepository.findOne(1L).isDeleted(),is(true));
    }
}
