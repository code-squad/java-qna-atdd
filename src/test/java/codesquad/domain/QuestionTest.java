package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {

    Question question;
    User user;

    @Before
    public void init(){
        question = new Question("a", "b");
        user = new User(0, "ryudung" ,"","","");
    }

    @Test
    public void update() throws UnAuthenticationException {
        question.writeBy(user);
        QuestionDto questionDto = new QuestionDto("title","contents");

        question.update(user, questionDto.toQuestion());

        assertThat(question.isContentsEquals(questionDto.toQuestion()) , is(true));
    }

    @Test
    public void delete() throws CannotDeleteException {
        question.writeBy(user);
        question.delete(user);

        assertThat(question.isDeleted(), is(true));
    }
}
