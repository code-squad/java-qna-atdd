package codesquad.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.dto.QuestionDto;
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

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final String ADI_QUESTION_BASE_PATH = "/api/questions";

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswerRepository answerRepository;

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Before
    public void setUp() {
        htmlFormDataBuilder = HtmlFormDataBuilder.urlEncodedForm();
    }

    @Test
    public void 질문_목록_조회() {
        ResponseEntity<String> response = getResource(ADI_QUESTION_BASE_PATH, String.class);
        Assertions.assertThat(response.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON_UTF8);
    }

    @Test
    public void 질문_상세_조회() {
        ResponseEntity<String> response = getResource(
                String.format("%s/1", ADI_QUESTION_BASE_PATH), String.class);

        assertResponseStatus(response, HttpStatus.OK);
        Assertions.assertThat(response.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON_UTF8);
    }

    @Test
    public void 로그인_사용자_질문_작성() {
        QuestionDto question = new QuestionDto("hello", "world");
        String resourceLocation = createResource(ADI_QUESTION_BASE_PATH, question, defaultUser());

        Assertions.assertThat(resourceLocation).startsWith("/api/questions/");
    }

    @Test
    public void 로그인_사용자_유효하지_않은_질문작성() {
        QuestionDto wrongQuestion = new QuestionDto("", "");
        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity(ADI_QUESTION_BASE_PATH, wrongQuestion, String.class);

        assertResponseStatus(response, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void 비로그인_사용자_질문_작성_시도() {
        QuestionDto question = new QuestionDto("hello", "world");
        ResponseEntity<String> response = template()
                .postForEntity(ADI_QUESTION_BASE_PATH, question, String.class);

        assertResponseStatus(response, HttpStatus.FORBIDDEN);
    }

    @Test
    public void 본인_질문_수정() {
        //given
        QuestionDto originQuestion = new QuestionDto("origin-title", "origin-contents");
        Question savedQuestion = questionRepository.save(new Question(defaultUser(), originQuestion));
        QuestionDto updateQuestion = new QuestionDto("new-title", "new-contents");

        //when
        basicAuthTemplate().put(savedQuestion.resourceUrl(), updateQuestion);

        //then
        ResponseEntity<String> response = getResource(savedQuestion.resourceUrl(), String.class);

        Assertions.assertThat(response.getBody()).doesNotContain(originQuestion.getTitle());
        Assertions.assertThat(response.getBody()).doesNotContain(originQuestion.getContents());

        Assertions.assertThat(response.getBody()).contains(updateQuestion.getTitle());
        Assertions.assertThat(response.getBody()).contains(updateQuestion.getContents());
    }

    @Test
    public void 타인_질문_수정_시도() {
        //given
        QuestionDto originQuestion = new QuestionDto("origin-title", "origin-contents");
        Question savedQuestion = questionRepository.save(new Question(defaultUser(), originQuestion));
        QuestionDto updateQuestion = new QuestionDto("new-title", "new-contents");

        //when
        basicAuthTemplate(findByUserId("sanjigi")).put(savedQuestion.resourceUrl(), updateQuestion);

        //then
        ResponseEntity<String> response = getResource(savedQuestion.resourceUrl(), String.class);

        Assertions.assertThat(response.getBody()).contains(originQuestion.getTitle());
        Assertions.assertThat(response.getBody()).contains(originQuestion.getContents());

        Assertions.assertThat(response.getBody()).doesNotContain(updateQuestion.getTitle());
        Assertions.assertThat(response.getBody()).doesNotContain(updateQuestion.getContents());
    }

    @Test
    public void 본인_질문_삭제() {
        //given
        QuestionDto originQuestion = new QuestionDto("origin-title", "origin-contents");
        Question savedQuestion = questionRepository.save(new Question(defaultUser(), originQuestion));
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        //when
        ResponseEntity<String> response = basicAuthTemplate()
                .exchange(savedQuestion.resourceUrl(), HttpMethod.DELETE, request, String.class);

        //then
        assertResponseStatus(response, HttpStatus.NO_CONTENT);
    }

    @Test
    public void 타인의_답변이_있는_질문_삭제_시도() {
        //given
        QuestionDto originQuestion = new QuestionDto("origin-title", "origin-contents");
        Question savedQuestion = questionRepository.save(new Question(defaultUser(), originQuestion));

        String content = "my answer";
        Answer othersAnswer = new Answer(findByUserId("sanjigi"), content);
        othersAnswer.toQuestion(savedQuestion);

        Answer savedAnswer = answerRepository.save(othersAnswer);
        savedQuestion.addAnswer(savedAnswer);

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        //when
        ResponseEntity<String> response = basicAuthTemplate()
                .exchange(savedQuestion.resourceUrl(), HttpMethod.DELETE, request, String.class);

        //then
        assertResponseStatus(response, HttpStatus.FORBIDDEN);
    }

    @Test
    public void 타인_질문_삭제_시도() {
        //given
        QuestionDto originQuestion = new QuestionDto("origin-title", "origin-contents");
        String resourceLocation = createResource(ADI_QUESTION_BASE_PATH, originQuestion, defaultUser());
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();

        //when
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(resourceLocation, HttpMethod.DELETE, request, String.class);

        //then
        assertResponseStatus(response, HttpStatus.FORBIDDEN);
    }
}