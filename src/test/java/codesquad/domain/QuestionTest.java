package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest  extends AcceptanceTest {
    private static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
    private static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void update_test(){
        Question question = questionRepository.findOne(1l);
        QuestionDto questionDto = new QuestionDto(1l, "test", "contentTest");

        question.update(JAVAJIGI,questionDto);

        assertThat(question.getTitle(), is("test"));
        assertThat(question.getContents(), is("contentTest"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void checkout_owner_test(){
        Question question = questionRepository.findOne(1l);
        QuestionDto questionDto = new QuestionDto(1l, "test", "contentTest");

        question.update(SANJIGI,questionDto);
    }

    @Test
    public void delete_test() throws Exception{
        Question question = questionRepository.findOne(1l);
        question.delete(JAVAJIGI);

        assertThat(question.isDeleted(), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_unAuth_test() throws Exception{
        Question question = questionRepository.findOne(1l);

        question.delete(SANJIGI);
    }


}
