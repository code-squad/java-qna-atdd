package codesquad.domain;

import codesquad.CannotDeleteException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AnswersTest {
    private static final Logger log =  LoggerFactory.getLogger(AnswersTest.class);

    private User user;
    private Answer answer;
    private Answers answers;

    @Before
    public void setUp() {
        user = new User("learner", "password", "taewon", "eamil@email.com");
        answer = new Answer(user, "요구사항을 잘 확인하자.");
    }

    @Test
    public void addAnswer() {
        answers = new Answers();
        answers.add(answer);
        assertThat(answers.getSize() > 0, is(true));
    }

    @Test
    public void getSize() {
        answers = new Answers();
        assertThat(answers.getSize(), is(0));
        answers.add(answer);
        assertThat(answers.getSize(), is(1));
    }

    @Test
    public void getString() {
        answers = new Answers();
        answers.add(answer);
        log.debug(answers.toString());
    }

    @Test
    public void hasOtherOwner(){
        User otherUser = new User("pobi", "password", "jaesung", "eamil@email.com");
        Answer otherAnswer = new Answer(otherUser, "TDD의 정수를 알려주마.");

        answers = new Answers();
        answers.add(answer);
        answers.add(otherAnswer);

        assertThat(answers.hasOtherOwner(user), is(true));
    }

    @Test
    public void update() {
        Answer updateAnswer = new Answer(user, "변경된 요구사항을 잘 확인하자.");
        answer.update(user, updateAnswer);
        assertThat(answer.getContents().equals(updateAnswer.getContents()), is(true));
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAll_fail() throws CannotDeleteException {
        User otherUser = new User("pobi", "password", "jaesung", "eamil@email.com");
        Answer otherAnswer = new Answer(otherUser, "TDD의 정수를 알려주마.");

        answers = new Answers();
        answers.add(answer);
        answers.add(otherAnswer);

        answers.deleteAll(user);
    }
}
