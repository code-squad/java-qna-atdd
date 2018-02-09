package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 실패_로그인_없이_질문_작성() {
        ResponseEntity<String> response = template()
                .getForEntity("/questions/form", String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 실패_로그인_없이_질문하기() {
        HttpEntity<MultiValueMap<String, Object>> request = defaultQuestionRequest("title", "content");
        ResponseEntity<String> response = template()
                .postForEntity("/questions/", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문하기(){
        Question question = new Question("title", "content");
        HttpEntity<MultiValueMap<String, Object>> request = defaultQuestionRequest(question.getTitle(), question.getContents());
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity("/questions/", request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(question.getContents()), is(true));
    }

    @Test
    public void 실패_로그인_없어_질문_수정_접근() {
        Question question = questionRepository.findOne(defaultUser().getId());
        ResponseEntity<String> response = template()
                .getForEntity(format("/questions/%d/form", question.getId()), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 실패_다른_사용자_질문_수정_접근() {
        Question question = questionRepository.findOne(defaultUser().getId());
        ResponseEntity<String> response = basicAuthTemplate(differentUser())
                .getForEntity(format("/questions/%d/form", question.getId()), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void 질문_수정_접근() {
        Question question = questionRepository.findOne(defaultUser().getId());
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .getForEntity(format("/questions/%d/form", question.getId()), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(question.getContents()), is(true));
    }

    @Test
    public void 질문_수정() {
        String updatedContents = "updatedContents";
        Question question = questionRepository.findOne(defaultUser().getId());
        HttpEntity<MultiValueMap<String, Object>> request = defaultQuestionRequest(question.getTitle(), updatedContents);
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .postForEntity(format("/questions/%d/update", question.getId()), request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(question.getContents()), is(false));
        assertThat(response.getBody().contains(updatedContents), is(true));
    }

    private HttpEntity<MultiValueMap<String, Object>> defaultQuestionRequest(String title, String content) {
        return HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", title)
                .addParameter("contents", content)
                .build();
    }

    // step4
    @Test
    public void 살패_질문_삭제() {

    }
}
