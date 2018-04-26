package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(QuestionAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;
    private User user;

    @Before
    public void setUp() throws Exception {
        user = defaultUser();
    }

    @Test
    public void 리스트_비로그인유저() throws Exception {
        ResponseEntity<String> response = template().getForEntity("/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
         assertThat(response.getBody().contains(defaultQuestion(defaultUser()).getTitle()), is(true));
    }
    @Test
    @Transactional
    public void 리스트_로그인유저() throws Exception {
        ResponseEntity<String> response = basicAuthTemplate(user)
                                          .getForEntity("/questions", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(defaultQuestion(user).getTitle()), is(true));
    }

    @Test
    public void 질문_생성_로그인유저() throws Exception {
        String title = "질문생성테스트";
        ResponseEntity<String> response = create(title, basicAuthTemplate(user));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertNotNull(questionRepository.findOne(user.getId()));
        assertThat(response.getHeaders().getLocation().getPath(), is("/questions/create"));
    }

    @Test
    public void 질문_생성_비로그인유저() throws Exception {
        ResponseEntity<String> response = create("질문생성테스트", template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> create(String title, TestRestTemplate testRestTemplate) {
        return testRestTemplate
                .postForEntity("/questions/create", HtmlFormDataBuilder.urlEncodedForm()
                        .addParameter("title", title)
                        .addParameter("contents", "질문생성테스트_컨텐츠")
                        .build(), String.class);
    }

    @Test
    @Transactional
    public void  업데이트_로그인유저() throws Exception {
        ResponseEntity<String> response = update(defaultQuestion(user), basicAuthTemplate(user));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/home"));
     }
    @Test
    @Transactional
    public void  업데이트_다른계정_로그인유저() throws Exception {
        ResponseEntity<String> response = update(defaultQuestion(user), basicAuthTemplate(anotherUser()));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }
    @Test
    @Transactional
    public void  업데이트_비로그인유저() throws Exception {
        ResponseEntity<String> response = update(defaultQuestion(user), template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> update(Question question, TestRestTemplate template) {
        return template
                .postForEntity(String.format("/questions/%d", question.getId()), HtmlFormDataBuilder
                        .urlEncodedForm()
                        .put()
                        .addParameter("title", "수정할 제목 내용")
                        .addParameter("contents", "수정할 컨텐츠 내용")
                        .build(), String.class);
    }

    @Test
    @Transactional
    public void 상세보기_비로그인() throws Exception {
        Question question = defaultQuestion(user);
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d",question.getId()), String.class);
        log.debug("body : {}", response.getBody());
        assertThat(response.getBody().contains(question.getContents()), is(true));
    }

    @Test
    @Transactional
    public void  삭제_로그인유저() throws Exception {
        ResponseEntity<String> response = delete(defaultQuestion(user), basicAuthTemplate(user));
        assertThat(response.getStatusCode(), is(HttpStatus.FOUND));
        assertTrue(response.getHeaders().getLocation().getPath().startsWith("/home"));
    }

    @Test
    @Transactional
    public void  삭제_다른계정_로그인유저() throws Exception {
        ResponseEntity<String> response = delete(defaultQuestion(user), basicAuthTemplate(anotherUser()));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    @Transactional
    public void  삭제_비로그인유저() throws Exception {
        ResponseEntity<String> response = delete(defaultQuestion(user), template());
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    private ResponseEntity<String> delete(Question question, TestRestTemplate template) {
        return template
                .postForEntity(String.format("/questions/%d", question.getId()), HtmlFormDataBuilder
                        .urlEncodedForm()
                        .delete()
                        .build(), String.class);
    }

    @Test
    @Transactional
    public void 도메인_질문_UPDATE_테스트() throws Exception {
        User user = defaultUser();
        Question question = defaultQuestion(user);
        question.update(user, new QuestionDto("업데이트테스트타이틀","업데이트테스트컨텐츠").toQuestion());
        assertThat(question.getTitle(),is("업데이트테스트타이틀"));
 }

    @Test
    @Transactional
    public void 도메인_isOwner_테스트_isOwnerTrue() throws Exception {
        User user   = defaultUser();
        Question question = defaultQuestion(user);
        assertThat(question.isOwner(user),is(true));
    }
    @Test
    @Transactional
    public void 도메인_isOwner_테스트_isOwnerFalse() throws Exception {
        User anotherUser = anotherUser();
        Question question = defaultQuestion(user);
        assertThat(question.isOwner(anotherUser),is(false));
    }
}