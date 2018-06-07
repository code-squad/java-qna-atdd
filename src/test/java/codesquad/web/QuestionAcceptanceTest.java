package codesquad.web;

import codesquad.domain.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionAcceptanceTest extends AcceptanceTest{
    private final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    private HttpEntity<MultiValueMap<String, Object>> request;
    private ResponseEntity<String> response;

    @Autowired
    private UserRepository userRepository;

    private Question defaultQuestion;

    @Test
    public void cannot_read_for_inappropriate_question() {
        Long noQuestionId = 10L;
        response = template()
                .getForEntity(String.format("/questions/%d", noQuestionId), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void can_read_for_login_user() {
        defaultQuestion = defaultQuestion();
        response = basicAuthTemplate()
                .getForEntity(defaultQuestion.generateUrl(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void can_read_for_guest_user() {
        defaultQuestion = defaultQuestion();
        response = template()
                .getForEntity(defaultQuestion.generateUrl(), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void can_show_create_form_for_login_user() {
        response = basicAuthTemplate()
                .getForEntity("/questions/form", String.class);
        log.debug("body : {}", response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertTrue(response.getBody().contains("제목"));
    }

    @Test
    public void cannot_show_create_form_for_guest_user() {
        response = template()
                .getForEntity("/questions/form", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN)); // 그냥 이렇게 막혀버리면 login 화면을 어떻게 보여주지?
        log.debug("response : {}",response);
    }

    @Test
    public void can_create_for_login_user() {
        request = createQuestionReq();

        response = basicAuthTemplate().postForEntity("/questions", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test // 이 테스트를 해야하나?
    public void cannnot_create_for_guest_user() {
        request = createQuestionReq();

        response = template().postForEntity("/questions", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void can_show_update_form_for_owner() {
        defaultQuestion = defaultQuestion();
        response = basicAuthTemplate()
                .getForEntity(String.format("%s/form", defaultQuestion.generateUrl()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void cannot_show_update_form_for_other_user() {
        defaultQuestion = defaultQuestion();
        User otherUser = userRepository.findByUserId("sanjigi").get();
        response = basicAuthTemplate(otherUser)
                .getForEntity(String.format("%s/form", defaultQuestion.generateUrl()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void cannot_show_update_form_for_guest_user() {
        defaultQuestion = defaultQuestion();
        response = template()
                .getForEntity(String.format("%s/form", defaultQuestion.generateUrl()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void can_update_for_owner() {
        defaultQuestion = defaultQuestion();
        request = updateQuestionReq();

        response = basicAuthTemplate().exchange(defaultQuestion.generateUrl(), HttpMethod.PUT, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void cannot_update_for_other_user() {
        defaultQuestion = defaultQuestion();
        User otherUser = userRepository.findByUserId("sanjigi").get();
        request = updateQuestionReq();

        response = basicAuthTemplate(otherUser).exchange(defaultQuestion.generateUrl(), HttpMethod.PUT, request, String.class);
        log.debug("response : {}", response);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));

    }

    @Test
    public void cannot_update_for_guest_user() {
        defaultQuestion = defaultQuestion();
        request = updateQuestionReq();

        response = template().exchange(defaultQuestion.generateUrl(), HttpMethod.PUT, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));

    }

    @Test
    public void cannot_delete_for_owner_and_other_answers() {
        HttpEntity entity = getHttpEntity();
        defaultQuestion = defaultQuestion();

        response = basicAuthTemplate().exchange(defaultQuestion.generateUrl(), HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void can_delete_for_owner_and_owner_answers() {
        HttpEntity entity = getHttpEntity();
        Question question = findById(2L);
        String userId = "sanjigi";

        response = basicAuthTemplate(findByUserId(userId)).exchange(question.generateUrl(), HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
    }

    @Test
    public void cannot_delete_for_other_user() {
        HttpEntity entity = getHttpEntity();
        defaultQuestion = defaultQuestion();
        User otherUser = userRepository.findByUserId("sanjigi").get();

        response = basicAuthTemplate(otherUser).exchange(defaultQuestion.generateUrl(), HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void cannot_delete_for_guest_user() {
        HttpEntity entity = getHttpEntity();
        defaultQuestion = defaultQuestion();

        response = template().exchange(defaultQuestion.generateUrl(), HttpMethod.DELETE, entity, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

}
