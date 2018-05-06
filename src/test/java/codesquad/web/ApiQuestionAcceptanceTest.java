package codesquad.web;


import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.dto.QuestionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ApiQuestionAcceptanceTest extends AcceptanceTest {

    private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    private Question myQuestion;
    private Question otherQuestion;

    /**
     * [질문]
     * - Question 도메인 클래스에 default constructor를 만들어 주지않으면 아래와같은 에러가 발생합니다.
     * 현상: no suitable constructor found, can not deserialize from Object value
     *
     * //        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
     * //        params.add("updatingQuestion", updatingQuestion); // 객체 맵핑이 안됨 why?
     */

    @Before
    public void init() {
        myQuestion = questionRepository.findOne(1L);
        otherQuestion = questionRepository.findOne(2L);
    }

    /**
     * input : void
     * output : 질문 목록
     */
    @Test
    public void 질문목록조회() throws IOException {
        ResponseEntity<String> response = template().getForEntity("/api/questions", String.class);

        ObjectMapper mapper = new ObjectMapper();
        log.debug("list: {}, list.size(): {}", response.getBody(), mapper.readValue(response.getBody(), List.class).size()); // 질문 목록 확인

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    /**
     * input : 질문 ID
     * output : 해당 질문 객체
     */
    @Test
    public void 질문상세조회() throws IOException {

        ResponseEntity<String> response = template().getForEntity(myQuestion.generateResourceURI(), String.class);
        log.debug("body: {}", response.getBody());

        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        ObjectMapper mapper = new ObjectMapper();
        assertThat(myQuestion.equals(mapper.readValue(response.getBody(), Question.class)), is(true));

    }

    /**
     * input : 등록할 질문 객체
     * output : 질문 Resource URI
     */
    @Test
    public void 질문작성_로그인사용자() {
        QuestionDto questionDto = new QuestionDto("title", "contents");

        String resourceLocation = createResourceByLoginUser("/api/questions", questionDto);
        Question savedQuestion = getResource(resourceLocation, Question.class, defaultUser());

        assertThat(questionDto.equals(savedQuestion.toQuestionDto()), is(true));
    }

    /**
     * input : 등록할 질문 객체
     * output : HTTP 403 Error(Forbidden)
     */
    @Test
    public void 질문작성_비로그인사용자() {
        QuestionDto questionDto = new QuestionDto("title", "contents");

        ResponseEntity<String> response = template().postForEntity("/api/questions", questionDto, String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    /**
     * input : 수정할 질문 객체
     * output : void
     */
    @Test
    public void 질문수정_자신의글() throws IOException {
        QuestionDto updatingQuestion = new QuestionDto("title2", "contents2");

        basicAuthTemplate().put(myQuestion.generateResourceURI(), updatingQuestion);

        ResponseEntity<String> response = basicAuthTemplate().getForEntity(myQuestion.generateResourceURI(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        // JSON -> Object 변경 후 비교
        ObjectMapper mapper = new ObjectMapper();
        assertThat(updatingQuestion.equals(mapper.readValue(response.getBody(), Question.class).toQuestionDto()), is(true));
    }

    /**
     * input : 수정할 질문 객체
     * output : HTTP 403 Error(Forbidden)
     */
    @Test
    public void 질문수정_타인의글() {
        QuestionDto updatingQuestion = new QuestionDto("title2", "contents2");

        ResponseEntity<String> response = basicAuthTemplate().exchange(otherQuestion.generateResourceURI(), HttpMethod.PUT, new HttpEntity(updatingQuestion), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    /**
     * input : 질문 ID
     * output : HTTP 204 (No Content)
     */
    @Test
    public void 질문삭제_자신의글() {

        ResponseEntity<String> response = basicAuthTemplate().exchange(
                myQuestion.generateResourceURI(), HttpMethod.DELETE, new HttpEntity(null), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
    }

    /**
     * input : 질문 ID
     * output : HTTP 403 Error(Forbidden)
     */
    @Test
    public void 질문삭제_타인의글() {
        ResponseEntity<String> response = basicAuthTemplate().exchange(
                otherQuestion.generateResourceURI(), HttpMethod.DELETE, new HttpEntity(null), String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }
}