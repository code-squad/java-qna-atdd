package codesquad.web;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    AnswerRepository answerRepository;

    private Question registeredQuestion;

    @Before
    public void setUp() {
        registeredQuestion = questionRepository.findOne(1L).get();
        Assertions.assertThat(registeredQuestion).isNotNull();
    }

    @Test
    public void 답변을_등록한다() {
        String answerContents = "This is my answer.";
        String resourceLocation = createResource(
                String.format("%s/answers", registeredQuestion.resourceUrl()), answerContents, defaultUser());

        Assertions.assertThat(resourceLocation)
                .startsWith(String.format("%s/answers/", registeredQuestion.resourceUrl()));
    }

    @Test
    public void 타인_답변_삭제_시도() {
        //given
        String answerContents = "This is my answer.";
        String resourceLocation = createResource(
                String.format("%s/answers", registeredQuestion.resourceUrl()), answerContents, defaultUser());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON_UTF8);
        HttpEntity request = new HttpEntity<>(headers);

        //when
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(resourceLocation, HttpMethod.DELETE, request, String.class);

        //then
        assertResponseStatus(response, HttpStatus.FORBIDDEN);
    }

    @Test
    public void 자신의_답변을_삭제한다() {
        //given
        String answerContents = "This is my answer.";
        String resourceLocation = createResource(
                String.format("%s/answers", registeredQuestion.resourceUrl()), answerContents, defaultUser());
        HttpEntity request = generateAnswerCreateRequest("");

        //when
        ResponseEntity<String> response = basicAuthTemplate()
                .exchange(resourceLocation, HttpMethod.DELETE, request, String.class);

        //then
        assertResponseStatus(response, HttpStatus.NO_CONTENT);
    }

    @Test
    public void 자신의_답변을_수정한다() {
        //given
        String answerContents = "This is my answer.";
        String resourceLocation = createResource(
                String.format("%s/answers", registeredQuestion.resourceUrl()), answerContents, defaultUser());

        String updatedAnswerContents = "This is my updated answer.";
        HttpEntity updatedRequest = generateAnswerCreateRequest(updatedAnswerContents);

        //when
        ResponseEntity<String> response = basicAuthTemplate()
                .exchange(resourceLocation, HttpMethod.PUT, updatedRequest, String.class);

        //then
        assertResponseStatus(response, HttpStatus.OK);
    }

    @Test
    public void 타인_답변_수정_시도() {
        //given
        String answerContents = "This is my answer.";
        String resourceLocation = createResource(
                String.format("%s/answers", registeredQuestion.resourceUrl()), answerContents, defaultUser());

        String updatedAnswerContents = "This is my updated answer.";

        HttpEntity updatedRequest = generateAnswerCreateRequest(updatedAnswerContents);

        //when
        ResponseEntity<String> response = basicAuthTemplate(findByUserId("sanjigi"))
                .exchange(resourceLocation, HttpMethod.PUT, updatedRequest, String.class);

        //then
        assertResponseStatus(response, HttpStatus.FORBIDDEN);
    }

    private HttpEntity generateAnswerCreateRequest(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON_UTF8);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("content", content);

        return new HttpEntity<>(params, headers);
    }
}
