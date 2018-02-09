package codesquad.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnswersTest {
    private Answers answers;
    private User userA;

    @Before
    public void setUp() {
        answers = new Answers();
        userA = new User();
    }

    @Test
    public void add() {
        answers.add(makeNewUser());
        assertEquals(1, answers.count());
    }

    private Answer makeNewUser() {
        return new Answer(2L, User.GUEST_USER, new Question(), "contents");
    }

    @Test
    public void findAnswer() {
        Answer answer = makeNewUser();
        answers.add(answer);
        assertEquals(answer, answers.findAnswer(2L));
        assertNotEquals(answer, answers.findAnswer(1L));
    }

    @Test
    public void deleteBy() {
        Answer answer = makeNewUser();
        answers.add(answer);

        assertEquals(answer, answers.findAnswer(2L));
        answers.deleteBy(User.GUEST_USER, 0L);
    }

    @Test(expected = IllegalStateException.class)
    public void deleteByAnotherUserSoFailed() {
        Answer answer = makeNewUser();
        answers.add(answer);

        assertEquals(answer, answers.findAnswer(2L));
        answers.deleteBy(userA, 0L);
    }

    @Test(expected = IllegalStateException.class)
    public void deleteByAnotherAnswerSoFailed() {
        Answer answer = makeNewUser();
        answers.add(answer);

        assertEquals(answer, answers.findAnswer(2L));
        answers.deleteBy(User.GUEST_USER, 1L);
    }
}