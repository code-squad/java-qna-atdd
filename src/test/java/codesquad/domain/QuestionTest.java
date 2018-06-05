package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QuestionTest {
    private Question question1;
    private Question question2;

    private User user1;
    private User user2;

    @Before
    public void setUp() throws Exception {
        user1 = new User("colin", "password", "colin", "colin@codesqaud.kr");
        user2 = new User("jinbro", "password", "jinbro", "jinbro@codesqaud.kr");

        question1 = new Question("test", "test");
        question1.writeBy(user1);

        question2 = new Question("hi", "bye");
        question2.writeBy(user2);
    }

    @Test
    public void owner() {
        assertTrue(question1.isOwner(user1));
    }

    @Test
    public void not_owner() {
        assertFalse(question1.isOwner(user2));
    }

    @Test
    public void update() {
        question1.update(user1, question1.toQuestionDto());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_match_writer() {
        question1.update(user2, question1.toQuestionDto());
    }

    @Test
    public void deleted() throws Exception {
        question1.delete(user1);
        assertTrue(question1.isDeleted());
    }

    @Test
    public void not_delete() {
        assertFalse(question1.isDeleted());
    }

    @Test(expected = UnAuthorizedException.class)
    public void delete_fail_not_match_user() throws Exception {
        question1.delete(user2);
    }

    private Answer generateAnswer(User user) {
        return new Answer(user, "test contents");
    }

    @Test
    public void delete_including_my_answer() throws Exception {
        question1.addAnswer(generateAnswer(user1));
        question1.delete(user1);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_fail_exist_other_user_answer() throws Exception {
        question1.addAnswer(generateAnswer(user2));
        question1.delete(user1);
    }
}