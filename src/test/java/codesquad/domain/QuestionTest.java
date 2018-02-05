package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class QuestionTest {
    private static final Logger logger = LoggerFactory.getLogger(QuestionTest.class);

    private User notOwner;
    private User owner;
    private Question originQuestion;
    private Question target;

    @Before
    public void setup() {
        notOwner = new User(2, "javajigi", "password", "java", "java@jigi.com");
        owner = new User(1, "boobby", "pass", "boo", "boo@boo.com");
        originQuestion = new Question("test", "테스트중입니다.");
        originQuestion.writeBy(owner);
        target = new Question("good", "테스트가 성공적입니다.");
    }

    @Test
    public void update_owner() {
        originQuestion.update(owner, target);

        assertThat(originQuestion.getTitle(), is(target.getTitle()));
        assertThat(originQuestion.getContents(), is(target.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        originQuestion.update(notOwner, target);
    }

    @Test
    public void delete_owner() throws CannotDeleteException {
        originQuestion.delete(owner);

        assertTrue(originQuestion.isDeleted());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_not_owner() throws CannotDeleteException {
        originQuestion.delete(notOwner);
    }
}