package codesquad.domain;

import codesquad.CannotDeleteException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnswerTest {
    private static final Logger log = LoggerFactory.getLogger(AnswerTest.class);

    private Answer answer;
    private User user;
    private User otherUser;

    @Before
    public void setUp() throws Exception {
        user = new User("jinbro", "1234", "jinbro", "jinbro@codesquad.kr");
        otherUser = new User("colin", "4567", "colin", "colin@codesquad.kr");
        answer = new Answer(user, "test contents");
    }

    @Test
    public void owner() {
        assertTrue(answer.isOwner(user));
    }

    @Test
    public void not_owner() {
        assertFalse(answer.isOwner(otherUser));
    }

    @Test
    public void is_delete() throws Exception {
        answer.delete(user);
        assertTrue(answer.isDeleted());
    }

    @Test
    public void delete() throws Exception {
        DeleteHistory history = answer.delete(user);
        log.debug("delete history : {}", history);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_fail_not_match_writer() throws Exception {
        answer.delete(otherUser);
    }
}