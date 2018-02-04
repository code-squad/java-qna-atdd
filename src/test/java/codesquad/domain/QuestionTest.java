package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QuestionTest {

    private Question savedQuestion;
    private User loginUser;

    @Before
    public void setUp() throws Exception {
        this.loginUser = UserTest.JAVAJIGI;
        this.savedQuestion = new Question("saved title", "saved contents");
        this.savedQuestion.writeBy(loginUser);
    }

    @Test
    public void 새로운_질문() throws Exception {
        String newTitle = "new title";
        String newContents = "new contents";
        Question question = new Question(newTitle, newContents);
        question.writeBy(loginUser);

        assertThat(question.getTitle(), is(newTitle));
        assertThat(question.getContents(), is(newContents));
        assertThat(question.getWriter(), is(UserTest.JAVAJIGI));
    }

    @Test
    public void 질문_수정() throws Exception {
        String updateTitle = "update title";
        String updateContents = "update contents";
        savedQuestion.update(loginUser, new QuestionDto(updateTitle, updateContents));

        assertThat(savedQuestion.getTitle(), is(updateTitle));
        assertThat(savedQuestion.getContents(), is(updateContents));
        assertThat(savedQuestion.getWriter(), is(loginUser));
    }

    @Test(expected = UnAuthorizedException.class)
    public void 다른_사람_질문_수정_익셉션() throws Exception {
        String updateTitle = "update title";
        String updateContents = "update contents";
        savedQuestion.update(UserTest.SANJIGI, new QuestionDto(updateTitle, updateContents));
    }

    @Test
    public void 질문_삭제() throws Exception {
        savedQuestion.delete(loginUser);
        assertTrue(savedQuestion.isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void 다른_사람_질문_삭제_익셉션() throws Exception {
        savedQuestion.delete(UserTest.SANJIGI);
        assertFalse(savedQuestion.isDeleted());
    }

    @Test
    public void 질문자와_답변글의_모든_답변자_같은경우() throws Exception {
        Answer answer1 = new Answer(1L, loginUser, null, "answer1");
        Answer answer2 = new Answer(2L, loginUser, null, "answer2");
        savedQuestion.addAnswer(answer1);
        savedQuestion.addAnswer(answer2);
        savedQuestion.delete(loginUser);
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문자와_답변글의_답변자_다른경우() throws Exception {
        Answer answer1 = new Answer(1L, UserTest.SANJIGI, null, "answer1");
        Answer answer2 = new Answer(2L, loginUser, null, "answer2");
        savedQuestion.addAnswer(answer1);
        savedQuestion.addAnswer(answer2);
        savedQuestion.delete(loginUser);
    }
}
