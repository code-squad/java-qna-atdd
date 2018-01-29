package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static codesquad.utils.HtmlFormDataBuilder.urlEncodedForm;
import static org.junit.Assert.assertEquals;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private Question question;

    @Autowired
    private QuestionRepository questionRepository;

    @Before
    public void init() {
        questionRepository.deleteAll();
        makeQuestion("TestTitle", "테스트당");
    }

    private Question makeQuestion(String title, String contents) {
        User javajigi = findByUserId("javajigi");
        question = new Question(title, contents);
        question.writeBy(javajigi);
        return question;
    }

    private HttpEntity<MultiValueMap<String, Object>> makQnaRequest(String ti, String contents) {
        return urlEncodedForm()
                .addParameter("title", ti)
                .addParameter("contents", contents)
                .build();
    }

    @Test
    public void 로그인한_사용자만_질문작성_가능한가() {
        ResponseEntity<String> response = template().postForEntity("/questions", makQnaRequest("제목입니다.", "내용입니다."), String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void 자신의_질문에_수정가능한가_통합_테스트() {
        Question saveQuestion = questionRepository.save(question);
        ResponseEntity<String> response = template().exchange("/questions/"+saveQuestion.getId(), HttpMethod.PUT, makQnaRequest("수정 제목입니다.", "수정 내용입니다."), String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void 자신의_질문에_삭제가능한가_통합_테스트() {
        Question saveQuestion = questionRepository.save(question);
        ResponseEntity<String> response = template().exchange("/questions/"+saveQuestion.getId(), HttpMethod.DELETE, urlEncodedForm().build(),  String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
