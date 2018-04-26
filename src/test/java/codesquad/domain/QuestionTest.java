package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;

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
        assertTrue(originalQuestion.isOwner(JAVAJIGI));
    }

    @Test
    public void certify_not_Writer() {
        originalQuestion.writeBy(JAVAJIGI);
        assertFalse(originalQuestion.isOwner(SANJIGI));
    }

}
