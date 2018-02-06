package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.QuestionDto;
import codesquad.service.QnAService;
import codesquad.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class QnAcceptanceTest extends AcceptanceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    QnAService qnAService;

    private User sanjigi;

    @Before
    public void setup() throws UnAuthenticationException {
        sanjigi = userService.login("sanjigi", "test");
    }

    //모든 사용자는 질문을 볼 수 있다.
    @Test
    public void anybody_can_access_to_question() {
        assertThat(Optional.of(qnAService.findAll()).isPresent(), is(true));
    }

    @Test
    public void acceptance_anybody_can_access_to_question() {
        ResponseEntity<String> response = template().getForEntity("/", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(sanjigi.getName()), is(true));
        assertThat(response.getBody().contains(defaultUser().getName()), is(true));
    }

    @Test
    public void acceptance_show_question() {
        long javajigiQuestion = defaultUser().getId();
        Question question = qnAService.findById(javajigiQuestion);
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d", question.getId()), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(question.getTitle()), is(true));
    }

    @Test
    public void acceptance_make_a_question() {
        ResponseEntity<String> response = template().getForEntity("/questions/form", String.class);
        assertThat(response.getBody().contains("action=\"/questions\""), is(true));
    }

    //로그인한 사용자에 대해서만 질문이 가능하다.
    @Test (expected = InvalidDataAccessApiUsageException.class)
    public void nobody_can_ask_a_question_without_login() {
        User question_without_login = new User("javajigi", "test", "name", "javajigi@slipp.net");
        qnAService.create(question_without_login, new QuestionDto("first", "test"));
    }

    @Test
    public void acceptance_nobody_can_ask_a_question_without_login(){
        Question question = new Question("first", "test");
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", question.getTitle())
                .addParameter("contents", question.getContents())
                .build();

        ResponseEntity<String> response = template().postForEntity("/questions/", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void create_question_success() {
        Question createdQuestion = qnAService.create(sanjigi, new QuestionDto("first", "test"));
        assertThat(createdQuestion.isOwner(sanjigi), is(true));
    }

    @Test
    public void acceptance_create_question_success_test_by_date() {
        Question question = new Question("first", "test");
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", question.getTitle())
                .addParameter("contents", question.getContents())
                .build();

        LocalDate now = LocalDate.now();
        ResponseEntity<String> response = basicAuthTemplate(sanjigi)
                .postForEntity("/questions/", request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))), is(true));
    }

    // 자신이 작성한 질문에 대해서만 수정/삭제가 가능하다.
    @Test
    public void acceptance_access_to_modify_question_not_login_user() {
        long sanjigiQuestion = sanjigi.getId();
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", sanjigiQuestion), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void acceptance_access_to_modify_question_different_user() {
        long sanjigiQuestion = sanjigi.getId();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(String.format("/questions/%d/form", sanjigiQuestion), String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

    @Test
    public void acceptance_access_to_modify_question() {
        long javajigiQuestion = defaultUser().getId();
        ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(String.format("/questions/%d/form", javajigiQuestion), String.class);
        assertThat(response.getBody().contains(String.format("action=\"/questions/%d/update\"",javajigiQuestion)), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_question_fail_nonExists_question() {
        Question noExistsQuestion = new Question("noExists", "cuz never created");
        qnAService.update(sanjigi, noExistsQuestion.getId(), noExistsQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_question_fail_different_user() throws UnAuthorizedException, UnAuthenticationException {
        Question createdQuestion = qnAService.create(sanjigi, new QuestionDto("test", "updatedTest"));

        User differentUser = userService.login("javajigi", "test");
        qnAService.update(differentUser, createdQuestion.getId(), createdQuestion);
    }

    @Test
    public void update_question_success_changed_same_writer() {
        String updateContent = "update text";
        Question createdQuestion = qnAService.create(sanjigi, new QuestionDto("first","test"));

        Question changedContent = new Question("first", updateContent);
        Question updatedQuestion = qnAService.update(sanjigi, createdQuestion.getId(), changedContent);

        assertThat(createdQuestion.getId(), is(updatedQuestion.getId()));
        assertThat(createdQuestion.isOwner(sanjigi), is(updatedQuestion.isOwner(sanjigi)));
    }

    @Test
    public void acceptance_update_question() {
        long sanjigiQuestion = sanjigi.getId();
        String updateContent = "update";
        Question question = qnAService.findById(sanjigiQuestion);
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", question.getTitle())
                .addParameter("contents", updateContent)
                .build();

        LocalDate now = LocalDate.now();
        ResponseEntity<String> response = basicAuthTemplate(sanjigi)
                .postForEntity(String.format("/questions/%d/update",question.getId()), request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(updateContent), is(true));
        assertThat(response.getBody().contains(now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))), is(true));
    }

    // step2이후의 단계네요...
    @Test (expected = UnAuthorizedException.class)
    public void delete_question_wrong_user() throws CannotDeleteException {
        long javajigiQuestion = defaultUser().getId();
        User sanjigi = this.sanjigi;

        qnAService.deleteQuestion(sanjigi, javajigiQuestion);
    }

    @Test (expected = IllegalArgumentException.class)
    public void delete_question_non_exists_question() throws IllegalArgumentException, CannotDeleteException {
        long noExistsQuestionId = 0l;
        qnAService.deleteQuestion(sanjigi, noExistsQuestionId);
    }

    @Test
    public void delete_question_success() throws CannotDeleteException {
        long questionId = sanjigi.getId();
        Question question = qnAService.findById(questionId);
        assertThat(qnAService.findById(questionId).isDeleted(), is(false));
        qnAService.deleteQuestion(sanjigi, question.getId());

        assertThat(qnAService.findById(questionId).isDeleted(), is(true));
        for (Question q : qnAService.findAll()) {
            assertThat(q.getId() == questionId, is(false));
        }
    }

    @Test
    public void acceptance_delete_question() {
        long javajigiQuestion = defaultUser().getId();

        String url = String.format("/questions/%d", javajigiQuestion);
        HttpEntity entity = new HttpEntity(HtmlFormDataBuilder.defaultHeaders());
        ResponseEntity<String> response = basicAuthTemplate(defaultUser())
                .exchange(url,HttpMethod.DELETE,entity,String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(defaultUser().getName()), is(false));
    }

    @Test (expected = CannotDeleteException.class)
    public void delete_question_already_deleted() throws CannotDeleteException {
        Question createdQuestion = qnAService.create(sanjigi, new QuestionDto("test", "updatedTest"));
        long questionId = createdQuestion.getId();

        assertThat(qnAService.findById(questionId).isDeleted(), is(false));
        qnAService.deleteQuestion(sanjigi, questionId);
        qnAService.deleteQuestion(sanjigi, questionId);
    }

    @Test (expected = IllegalArgumentException.class)
    public void add_answer_fail_no_question_in_db() throws CannotDeleteException {
        Question question = new Question("123", "123");
        qnAService.addAnswer(sanjigi, question.getId(),"최소다섯글자");
    }

    @Test (expected = UnAuthorizedException.class)
    public void delete_answer_fail_wrong_writer() throws CannotDeleteException, UnAuthenticationException {
        long javajigiQuestion = defaultUser().getId();
        User sanjigi = this.sanjigi;

        Answer sanjigiAnswer = qnAService.addAnswer(sanjigi, javajigiQuestion, "답변을 남긴다.");

        User javajigi = userService.login(defaultUser().getUserId(), defaultUser().getPassword());
        qnAService.deleteAnswer(javajigi, sanjigiAnswer.getId());
    }

    @Test (expected = CannotDeleteException.class)
    public void delete_answer_already_deleted() throws CannotDeleteException {
        long javajigiQuestion = defaultUser().getId();
        User sanjigi = this.sanjigi;

        Answer sanjigiAnswer = qnAService.addAnswer(sanjigi, javajigiQuestion, "답변을 남긴다.");
        qnAService.deleteAnswer(sanjigi, sanjigiAnswer.getId());
        qnAService.deleteAnswer(sanjigi, sanjigiAnswer.getId());
    }

    @Test
    public void delete_answer_success_right_writer_but_different_owner_question() throws CannotDeleteException {
        long javajigiQuestion = defaultUser().getId();
        User sanjigi = this.sanjigi;

        Answer sanjigiAnswer = qnAService.addAnswer(sanjigi, javajigiQuestion, "답변을 남긴다.");
        qnAService.deleteAnswer(sanjigi, sanjigiAnswer.getId());
    }
}