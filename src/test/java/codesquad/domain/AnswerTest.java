package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class AnswerTest {

    public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    public Answer originalAnswer = new Answer(JAVAJIGI,"내용입니다요");

    @Test
    public void question_update_sucess() {
        AnswerDto update = new AnswerDto("내용입니다2");
        Answer updateAnswer = new Answer(SANJIGI,update.getContents());

        originalAnswer.update(JAVAJIGI,updateAnswer);
        assertThat(originalAnswer.getContents(), is("내용입니다2"));

    }

    @Test(expected = UnAuthorizedException.class)
    public void question_update_fail() {
        AnswerDto update = new AnswerDto("내용입니다2");
        Answer updateAnswer = new Answer(SANJIGI,update.getContents());

        originalAnswer.update(SANJIGI,updateAnswer);
    }
}
