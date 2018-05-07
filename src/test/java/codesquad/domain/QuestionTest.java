package codesquad.domain;

import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class QuestionTest {

    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    public Question originalQuestion = new Question("멈추면비로서보이는것들","내용입니다.");

    @Test
    public void question_update_sucess() {
        originalQuestion.writeBy(JAVAJIGI);
        Question updateQeustion = new Question("불행피하기기술","영리하게인생을움직이는52가지비밀");

        originalQuestion.update(JAVAJIGI,updateQeustion);

        assertThat(originalQuestion.getContents(),is(updateQeustion.getContents()));
        assertThat(originalQuestion.getWriter(),is(JAVAJIGI));
    }

    @Test(expected = UnAuthorizedException.class)
    public void question_update_fail() {
        originalQuestion.writeBy(JAVAJIGI);
        Question updateQeustion = new Question("불행피하기기술","영리하게인생을움직이는52가지비밀");

        originalQuestion.update(SANJIGI,updateQeustion);
    }

    @Test
    public void certifyWriter() {
        originalQuestion.writeBy(JAVAJIGI);
        assertTrue(originalQuestion.isQuestionOwner(JAVAJIGI));
    }

    @Test(expected = UnAuthorizedException.class)
    public void certify_not_Writer() {
        originalQuestion.writeBy(JAVAJIGI);
        assertFalse(originalQuestion.isQuestionOwner(SANJIGI));
    }

    @Test(expected = NullPointerException.class)
    public void delete_null_question() throws UnAuthenticationException {
        Question question = null;
        List<DeleteHistory> histories = question.deleteQuestion(JAVAJIGI);
        assertNotNull(histories);
    }

    @Test
    public void deleteQuestion_nullAnswers_sucess() throws UnAuthenticationException {
        Question question = new Question("무엇을 질문해야 할까", "궁금한걸 질문하자");
        question.writeBy(JAVAJIGI);
        List<DeleteHistory> histories = question.deleteQuestion(JAVAJIGI);
        assertNotNull(histories);
        assertThat(1, is(histories.size()));
    }

    @Test
    public void deleteQuestion_notNullAnswers_sucess() throws UnAuthenticationException {
        Question question = new Question("무엇을 질문해야 할까", "궁금한걸 질문하자");
        Answer answer1 = new Answer(JAVAJIGI,"답변이다1");
        Answer answer2 = new Answer(JAVAJIGI,"답변이다2");
        question.writeBy(JAVAJIGI);
        question.addAnswer(answer1);
        question.addAnswer(answer2);
        List<DeleteHistory> histories = question.deleteQuestion(JAVAJIGI);
        assertNotNull(histories);
        assertThat(3, is(histories.size()));

    }

    @Test(expected = UnAuthenticationException.class)
    public void deleteQuestion_notNullAnswers_fail() throws UnAuthenticationException {
        Question question = new Question("무엇을 질문해야 할까", "궁금한걸 질문하자");
        Answer answer1 = new Answer(SANJIGI,"답변이다1");
        Answer answer2 = new Answer(SANJIGI,"답변이다2");
        question.writeBy(JAVAJIGI);
        question.addAnswer(answer1);
        question.addAnswer(answer2);
        List<DeleteHistory> histories = question.deleteQuestion(JAVAJIGI);
        assertNotNull(histories);
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_notLoginUser_fail() throws UnAuthenticationException {
        Question question = new Question("무엇을 질문해야 할까", "궁금한걸 질문하자");

        question.writeBy(JAVAJIGI);
        List<DeleteHistory> histories = question.deleteQuestion(SANJIGI);
        assertNotNull(histories);
    }
}
