package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class QuestionTest {
    public Question sample;

    @Before
    public void setUp() throws Exception {
        sample = new Question("제목입니다아아아", "내용이지비요....");
        sample.writeBy(UserTest.JAVAJIGI);
    }

    @Test
    public void isOwner() {
        assertThat(sample.isOwner(UserTest.JAVAJIGI), is(true));
        assertThat(sample.isOwner(UserTest.SANJIGI), is(false));
    }

    @Test
    public void update() {
        Question target  = new Question("수정할 제목입니다아아아", "수정할 내용입니다.... 비교해 주세요~");
        sample.update(UserTest.JAVAJIGI, target);

        assertThat(sample.getTitle(), is(target.getTitle()));
        assertThat(sample.getContents(), is(target.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_unauthorized_user() {
        Question target  = new Question("수정할 제목입니다아아아", "수정할 내용입니다.... 비교해 주세요~");
        sample.update(UserTest.SANJIGI, target);
    }

    @Test
    public void delete() {
        sample.delete(UserTest.JAVAJIGI);
        assertThat(sample.isDeleted(), is(true));
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_unauthorized_user() {
        sample.delete(UserTest.SANJIGI);
    }
}