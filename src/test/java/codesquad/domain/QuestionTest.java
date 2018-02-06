package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by hoon on 2018. 2. 6..
 */
public class QuestionTest {

    Question question1;
    Question question2;

    User user1;
    User user2;

    @Before
    public void setup() {
        question1 = new Question("test1", "test1");
        user1 = new User(1, "test1", "test1", "test1", "test1");
        question1.writeBy(user1);

        question2 = new Question("test2", "test2");
        user2 = new User(2, "test2", "test2", "test2", "test2");
        question2.writeBy(user2);
    }

    @Test
    public void update() {
        question1.update(user1, new QuestionDto("new title", "new contents"));
        assertThat(question1.getTitle(), is("new title"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void UnAuthorizedException_익셉션_테스트() {
        question1.update(user2, new QuestionDto("wrong title", "wrong contents"));
    }

    @Test
    public void delete() {
        question2.delete(user2);
        assertTrue(question2.isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void CannotDeleteException_익셉션_테스트() {
        question2.delete(user1);
    }
}
