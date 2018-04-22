package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    @Autowired
    private QuestionRepository questionRepository;

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void setUp() {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void 질문_목록을_조회한다() {
        Question savedQuestion = saveQuestionWriteBy(defaultUser());

        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).contains(savedQuestion.getTitle());
    }

    @Test
    public void 질문_하나를_상세_조회한다() {
        Question savedQuestion = saveQuestionWriteBy(defaultUser());

        ResponseEntity<String> response = template()
                .getForEntity(savedQuestion.generateUrl(), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).contains(savedQuestion.getTitle());
        Assertions.assertThat(response.getBody()).contains(savedQuestion.getContents());
    }

    @Test
    public void 비로그인_사용자는_질문을_작성하지_못한다() {
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("title", "foo")
                .addParameter("contents", "bar")
                .build();

        ResponseEntity<String> response = template()
                .postForEntity("/questions", request, String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 비로그인_사용자는_질문_작성_폼을_얻지_못한다() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        System.out.println(response.getBody());
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 로그인_사용자는_질문_작성_폼을_얻는다() {
        ResponseEntity<String> response = basicAuthTemplate().getForEntity("/questions/form", String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void 로그인_사용자는_질문을_작성할_수_있다() {
        String title = "hello world";

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("title", title)
                .addParameter("contents", "bar")
                .build();

        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity("/questions", request, String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        Assertions.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/questions/");
    }

    @Test
    public void 타인의_질문_수정_폼을_얻지_못한다() {
        User other = findByUserId("sanjigi");
        Question question = saveQuestionWriteBy(other);

        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity(String.format("%s/form", question.generateUrl()), String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 자신의_질문_수정_폼을_얻는다() {
        Question question = saveQuestionWriteBy(defaultUser());

        ResponseEntity<String> response = basicAuthTemplate()
                .getForEntity(String.format("%s/form", question.generateUrl()), String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @Test
    public void 자신의_질문을_수정할_수_있다() {
        Question question = saveQuestionWriteBy(defaultUser());

        String updateTitle = question.getTitle() + " update";
        String updateContent = question.getContents() + " update";

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder
                .addParameter("title", updateTitle)
                .addParameter("contents", updateContent)
                .build();

        ResponseEntity<String> response = basicAuthTemplate()
                .exchange(question.generateUrl(), HttpMethod.PUT, request, String.class);

        Question updatedQuestion = questionRepository.findOne(question.getId()).get();
        Assertions.assertThat(updatedQuestion.getTitle()).isEqualTo(updateTitle);
        Assertions.assertThat(updatedQuestion.getContents()).isEqualTo(updateContent);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        Assertions.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo(updatedQuestion.generateUrl());
    }

    @Test
    public void 비로그인_사용자는_질문을_삭제하지_못한다() {
        Question question = saveQuestionWriteBy(defaultUser());
        Question savedQuestion = questionRepository.save(question);

        ResponseEntity<String> response = template()
                .exchange(savedQuestion.generateUrl(), HttpMethod.DELETE, new HttpEntity<>(""), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void 자신의_질문을_삭제할_수_있다() {
        ResponseEntity<String> response = basicAuthTemplate()
                .exchange("/questions/1", HttpMethod.DELETE, new HttpEntity<>(""), String.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        Assertions.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    private Question saveQuestionWriteBy(User user) {
        String title = "my-question-title";
        String content = "my-question-content";
        Question question = new Question(title, content);
        question.writeBy(user);

        return questionRepository.save(question);
    }
}
