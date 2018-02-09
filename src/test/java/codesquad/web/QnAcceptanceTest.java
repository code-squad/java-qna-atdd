package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.service.QnAService;
import codesquad.service.UserService;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;

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

    // step4
    @Test (expected = UnAuthorizedException.class)
    public void delete_question_wrong_user() {
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
    public void delete_question_success() {
        long questionId = sanjigi.getId();
        Question question = qnAService.findById(questionId);
        assertThat(qnAService.findById(questionId).isDeleted(), is(false));
        qnAService.deleteQuestion(sanjigi, question.getId());

        for (Question q : qnAService.findAll()) {
            assertThat(q.getId() == questionId, is(false));
        }
    }

    @Test
    public void acceptance_delete_question() {
        String createContents = "willBeDeleted";
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "title")
                .addParameter("contents", createContents)
                .build();

        ResponseEntity<String> response = basicAuthTemplate()
                .postForEntity("/questions/", request, String.class);
        assertThat(response.getBody().contains(createContents), is(true));

        Question createdQuestion = Lists.newArrayList(qnAService.findAll()).stream()
                .filter(question -> createContents.equals(question.getContents()))
                .findFirst()
                .get();

        String url = String.format("/questions/%d", createdQuestion.getId());
        HttpEntity entity = new HttpEntity(HtmlFormDataBuilder.defaultHeaders());
        response = basicAuthTemplate(defaultUser())
                .exchange(url,HttpMethod.DELETE,entity,String.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().contains(createdQuestion.getContents()), is(false));
    }

    @Test (expected = IllegalArgumentException.class)
    public void delete_question_already_deleted() {
        Question createdQuestion = qnAService.create(sanjigi, new QuestionDto("test", "updatedTest"));
        long questionId = createdQuestion.getId();

        assertThat(qnAService.findById(questionId).isDeleted(), is(false));
        qnAService.deleteQuestion(sanjigi, questionId);
        qnAService.deleteQuestion(sanjigi, questionId);
    }

    @Test (expected = UnAuthorizedException.class)
    public void delete_answer_fail_wrong_writer() throws UnAuthenticationException {
        long javajigiQuestion = defaultUser().getId();
        User sanjigi = this.sanjigi;

        Answer sanjigiAnswer = qnAService.addAnswer(sanjigi, new AnswerDto("답변을 남긴다.", javajigiQuestion));

        User javajigi = userService.login(defaultUser().getUserId(), defaultUser().getPassword());
        qnAService.deleteAnswer(javajigi, sanjigiAnswer.getId());
    }

    @Test (expected = IllegalArgumentException.class)
    public void delete_answer_already_deleted() {
        long javajigiQuestion = defaultUser().getId();
        User sanjigi = this.sanjigi;

        Answer sanjigiAnswer = qnAService.addAnswer(sanjigi, new AnswerDto("답변을 남긴다.", javajigiQuestion));
        qnAService.deleteAnswer(sanjigi, sanjigiAnswer.getId());
        qnAService.deleteAnswer(sanjigi, sanjigiAnswer.getId());
    }

    @Test
    public void delete_answer_success_right_writer_but_different_owner_question() {
        long javajigiQuestion = defaultUser().getId();
        User sanjigi = this.sanjigi;

        Answer sanjigiAnswer = qnAService.addAnswer(sanjigi, new AnswerDto("답변을 남긴다.", javajigiQuestion));
        qnAService.deleteAnswer(sanjigi, sanjigiAnswer.getId());
    }
}