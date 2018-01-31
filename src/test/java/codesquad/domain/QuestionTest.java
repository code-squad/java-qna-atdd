package codesquad.domain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

public class QuestionTest {
    private static final String TITLE_TEST = "title test";
    private static final String CONTENTS_TEST = "contents test";

    private Question question;
    private User userA;
    private User userB;

    @Before
    public void setUp() {
        question = new Question(TITLE_TEST, CONTENTS_TEST);
        userA = new User("google_id", "passwd1", "google", "google-mail");
        userB = new User("apple_id", "passwd2", "apple", "apple-mail");
    }

    @Test
    public void writeBy() {
        question.writeBy(userA);
        assertEquals(userA, question.getWriter());
        assertNotEquals(userB, question.getWriter());
    }

    @Test
    public void addAnswer() {
        // TODO : answer 구현 시에
    }

    @Test
    public void isOwner() {
        question.writeBy(userA);
        assertTrue(question.isOwner(userA));
        assertFalse(question.isOwner(userB));
    }

    @Test
    public void generateUrl() {
        assertEquals("/questions/0", question.generateUrl());
    }

    @Test
    public void updateBy() {
        question.writeBy(userA);
        Question updatedQuestion = new Question(TITLE_TEST + "1", CONTENTS_TEST + "2");
        question.updateBy(updatedQuestion, userA);
        assertEquals(TITLE_TEST + "1", question.getTitle());
        assertEquals(CONTENTS_TEST + "2", question.getContents());
    }

    @Test(expected = IllegalStateException.class)
    public void updateByFailedNotSameUser() {
        question.writeBy(userA);
        Question updatedQuestion = new Question(TITLE_TEST + "1", CONTENTS_TEST + "2");
        question.updateBy(updatedQuestion, userB);
    }

    @Test
    public void deleteBy() {
        question.writeBy(userA);
        question.deleteBy(userA);
        assertTrue(question.isDeleted());
    }

    @Test(expected = IllegalStateException.class)
    public void deleteByFailedNotSameUser() {
        question.writeBy(userA);
        question.deleteBy(userB);
    }

}