package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {

    Question question;
    User user;
    User user1;

    @Before
    public void init(){
        question = new Question("a", "b");

        user = new User(1, "ryudung" ,"","","");
        user1 = new User(2, "javajigi" ,"","","");
    }

    @Test
    public void update() throws UnAuthenticationException {
        questionWrite();
        QuestionDto questionDto = new QuestionDto("title","contents");

        question.update(user, questionDto.toQuestion());

        assertThat(question.isContentsEquals(questionDto.toQuestion()) , is(true));
    }

    @Test
    public void delete() throws CannotDeleteException {
        questionWrite();
        question.delete(user);

        assertThat(question.isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문한_사람과_로그인한_사람이_다른_경우() throws CannotDeleteException {
        questionWrite();
        question.delete(user1);
    }


    @Test
    public void 질문한_사람과_로그인한_사람이_같으면서_답변이_없는_경우_성공() throws CannotDeleteException {
        questionWrite();
        question.delete(user);

        assertThat(question.isDeleted(), is(true));
    }


    @Test
    public void 질문한_사람과_로그인한_사람이_같으면서_답변의_글쓴이도_같은_경우_성공() throws CannotDeleteException {
        questionWrite();
        addAnswer(1L, user, question, "답변");

        question.delete(user);
        assertThat(question.isDeleted(), is(true));
    }



    @Test(expected = UnAuthorizedException.class)
    public void 질문한_사람과_로그인한_사람이_같으면서_답변의_글쓴이가_다른_경우() throws CannotDeleteException {
        questionWrite();
        addAnswer(1L, user1, question, "답변");

        question.delete(user);
    }

    private void addAnswer(long l, User user1, Question question, String contents) {
        this.question.addAnswer(new Answer(l, user1, question, contents));
    }

    private void questionWrite() {
        question.writeBy(user);
    }
}
