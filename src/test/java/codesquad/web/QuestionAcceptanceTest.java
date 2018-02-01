package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import util.HtmlFormDataBuilder;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(LoginAcceptanceTest.class);

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    //create test를 위한 메소드
    private ResponseEntity<String> create(TestRestTemplate template) throws Exception {

        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
        htmlFormDataBuilder
                .addParameter("title", "세상에서 가장 쉬운 언어는 무엇인가요?")
                .addParameter("contents", "당연히 자바죠?");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        log.debug(request.toString());
        return template.postForEntity("/qna", request, String.class);
    }

    @Test
    public void createQuestionTest() throws Exception{
        ResponseEntity<String> response = create(basicAuthTemplate());
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        String title = questionRepository.findOne(3L).getTitle();
        assertThat(title, is("세상에서 가장 쉬운 언어는 무엇인가요?"));
    }

    @Test
    public void createQuestionFailTest_NoLogin() throws Exception{
        ResponseEntity<String> response = create(template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    //delete test를 위한 메소드
    private ResponseEntity<String> delete(TestRestTemplate template, long deleteTargetQuestionId) throws Exception {
        String questionId = Long.toString(deleteTargetQuestionId);
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.delete();
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        return template.postForEntity(String.format("/qna/%s/delete", questionId), request, String.class);
    }

    @Test
    public void deleteQuestionTest() throws Exception {
        long deleteTargetQuestionId = 1L;
        ResponseEntity<String> response = delete(basicAuthTemplate(), deleteTargetQuestionId);
        Question question = questionRepository.findOne(deleteTargetQuestionId);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertThat(question.isDeleted(), is(true));
    }

    @Test
    public void deleteQuestion_FailTest_NoLogin() throws Exception {
        long deleteTargetQuestionId = 1L;
        ResponseEntity<String> response = delete(template(), deleteTargetQuestionId);
        Question question = questionRepository.findOne(deleteTargetQuestionId);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void deleteQuestion_FailTest_AnotherUser() throws Exception {
        User logingUser = userRepository.findOne(2L);
        long deleteTargetQuestionId = 1L;
        ResponseEntity<String> response = delete(basicAuthTemplate(logingUser), deleteTargetQuestionId);
        log.debug(response.toString());
        Question question = questionRepository.findOne(deleteTargetQuestionId);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    //update test를 위한 메소드
    private ResponseEntity<String> update(TestRestTemplate template, long updateTargetQuestionId) throws Exception {
        String questionId = Long.toString(updateTargetQuestionId);
        HtmlFormDataBuilder htmlFormDataBuilder = HtmlFormDataBuilder.put();
        htmlFormDataBuilder.addParameter("title", "세상에서 가장 쉬운 언어는 무엇인가요?")
                            .addParameter("contents", "당연히 자바죠?");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        return template.postForEntity(String.format("/qna/%s/update", questionId), request, String.class);
    }

    @Test
    public void updateQuestionTest() throws Exception {
        long updateTargetQuestionId = 1L;
        ResponseEntity<String> response = update(basicAuthTemplate(), updateTargetQuestionId);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        String title = questionRepository.findOne(updateTargetQuestionId).getTitle();
        String contents = questionRepository.findOne(updateTargetQuestionId).getContents();
        assertThat(title, is("세상에서 가장 쉬운 언어는 무엇인가요?"));
        assertThat(contents, is("당연히 자바죠?"));
    }

    @Test
    public void updateQuestion_FailTest_NoLogin() throws Exception {
        long updateTargetQuestionId = 1L;

        ResponseEntity<String> response = update(template(), updateTargetQuestionId);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void updateQuestion_FailTest_AnotherUser() throws Exception {
        User tempLogingUser = userRepository.findOne(2L);
        long updateTargetQuestionId = 1L;
        ResponseEntity<String> response = update(basicAuthTemplate(tempLogingUser), updateTargetQuestionId);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }
}
