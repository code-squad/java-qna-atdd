package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.util.HtmlFormDataBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QuestionAcceptanceTest extends AcceptanceTest{
    @Autowired
    private QuestionRepository questionRepository;

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void setUp() {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void qnaList() {
        Question question = new Question(defaultUser(), "새로운 게시물입니다.", "새로운 내용입니다.");
        questionRepository.save(question);
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("새로운 게시물입니다."));
    }

    @Test
    public void showQuestion() {
        long questionId = 1;
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", questionId),String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void createForm() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void createQuestion() {
        User loginUser = defaultUser();
        htmlFormDataBuilder.addParameter("title", "타이틀입니다.");
        htmlFormDataBuilder.addParameter("contents", "내용입니다.");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity("/questions", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void updateForm() {
        User loginUser = defaultUser();
        long id = 1;
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity(String.format("/questions/%d/form", id), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void writerNotEqualUpdate() {
        User loginUser = defaultUser();
        long id = 2;
        ResponseEntity<String> response = basicAuthTemplate(loginUser).getForEntity(String.format("/questions/%d/form", id), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("작성자가 같은 질문만 수정 / 삭제할 수 있습니다."));
    }

    @Test
    public void updateQuestion() {
        User loginUser = defaultUser();
        long id = 1;
        htmlFormDataBuilder.addMethod("put");
        htmlFormDataBuilder.addParameter("title", "수정 타이틀입니다.");
        htmlFormDataBuilder.addParameter("contents", "수정 내용입니다.");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d",id), request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void deleteQuestion() {
        User loginUser = defaultUser();
        long id = 1;
        htmlFormDataBuilder.addMethod("delete");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d", id), request, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void writerNotEqualsDelete() {
        User loginUser = defaultUser();
        long id = 2;
        htmlFormDataBuilder.addMethod("delete");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d", id), request, String.class);

        assertTrue(response.getBody().contains("작성자가 같은 질문만 수정 / 삭제할 수 있습니다."));
    }
}
