package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    private Question saveQuestionBy(User user) {
        String title = "test";
        String contents = "contents";
        Question question = new Question(title, contents);
        question.writeBy(user);

        return questionRepository.save(question);

    }

    @Test
    public void 질문목록보기_비로그인사용자() {
        Question question = saveQuestionBy(defaultUser());
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void 질문목록보기_로그인사용자() {
        Question question = saveQuestionBy(defaultUser());
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(question.getTitle()), is(true));
    }

    @Test
    public void 질문작성폼_비로그인사용자() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문작성폼_로그인사용자() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void 질문작성_비로그인한사용자() throws Exception {
        ResponseEntity<String> response =
                template().postForEntity("/questions",
                        HtmlFormDataBuilder.urlEncodedForm()
                                .addParameter("title", "title")
                                .addParameter("contents", "contents")
                                .build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문작성_로그인한사용자() throws Exception {
        ResponseEntity<String> response =
                basicAuthTemplate().postForEntity("/questions",
                        HtmlFormDataBuilder.urlEncodedForm()
                                .addParameter("title", "title")
                                .addParameter("contents", "contents")
                                .build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
    }

    @Test
    public void 질문수정폼_비로그인한사용자() throws Exception {
        Question question = saveQuestionBy(defaultUser());

        StringBuilder url = new StringBuilder();
        url.append(question.generateUrl());
        url.append("/update");

        ResponseEntity<String> response =
                template().getForEntity(url.toString(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문수정폼_타인의글() throws Exception {
        User user = findByUserId("sanjigi");
        Question question = saveQuestionBy(user);

        StringBuilder url = new StringBuilder();
        url.append(question.generateUrl());
        url.append("/update");

        ResponseEntity<String> response =
                basicAuthTemplate().getForEntity(url.toString(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }


    @Test
    public void 질문수정폼_자신의글() throws Exception {
        Question question = saveQuestionBy(defaultUser());

        StringBuilder url = new StringBuilder();
        url.append(question.generateUrl());
        url.append("/update");

        ResponseEntity<String> response =
                basicAuthTemplate().getForEntity(url.toString(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void 질문수정_비로그인한사용자() throws Exception {
        Question question = saveQuestionBy(defaultUser());

        ResponseEntity<String> response =
                template().postForEntity(question.generateUrl(),
                        HtmlFormDataBuilder.urlEncodedForm()
                                .put()
                                .addParameter("title", "title")
                                .addParameter("contents", "contents")
                                .build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문수정_타인의글() throws Exception {
        User user = findByUserId("sanjigi");
        Question question = saveQuestionBy(user);

        ResponseEntity<String> response =
                basicAuthTemplate().postForEntity(question.generateUrl(),
                        HtmlFormDataBuilder.urlEncodedForm()
                                .put()
                                .addParameter("title", "title")
                                .addParameter("contents", "contents")
                                .build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문수정_자신의글() throws Exception {
        Question question = saveQuestionBy(defaultUser());

        ResponseEntity<String> response =
                basicAuthTemplate().postForEntity(question.generateUrl(),
                        HtmlFormDataBuilder.urlEncodedForm()
                                .put()
                                .addParameter("title", "title2")
                                .addParameter("contents", "contents2")
                                .build(), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));

        Question updatedQuestion = questionRepository.findOne(question.getId());
        assertThat(updatedQuestion.getTitle().equals("title2"), is(true));
        assertThat(updatedQuestion.getContents().equals("contents2"), is(true));

        assertThat(response.getHeaders().getLocation().getPath(), is("/questions"));
    }


    @Test
    public void 질문삭제_비로그인한사용자() throws Exception {
        Question question = saveQuestionBy(defaultUser());

        ResponseEntity<String> response =
                template().exchange(
                        question.generateUrl(),
                        HttpMethod.DELETE, new HttpEntity(new HttpHeaders()), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문삭제_타인의글() throws Exception {
        User user = findByUserId("sanjigi");
        Question question = saveQuestionBy(user);

        ResponseEntity<String> response =
                basicAuthTemplate().exchange(
                        question.generateUrl(),
                        HttpMethod.DELETE, new HttpEntity(new HttpHeaders()), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }


    @Test
    public void 질문삭제_자신의글() throws Exception {
        Question question = saveQuestionBy(defaultUser());

        ResponseEntity<String> response =
                basicAuthTemplate().exchange(
                        question.generateUrl(),
                        HttpMethod.DELETE, new HttpEntity(new HttpHeaders()), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }
}