package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class AnswerTest {
    private static final Logger log = LoggerFactory.getLogger(AnswerTest.class);

    private User loginUser;
    private Question savedQuestion;
    private Answer savedAnswer;

    @Before
    public void setUp() throws Exception {
        this.loginUser = UserTest.JAVAJIGI;
        this.savedQuestion = new Question("saved title", "saved contents");
        this.savedQuestion.writeBy(loginUser);
        this.savedAnswer = new Answer(1L, loginUser, savedQuestion, "answer");
    }

    @Test
    public void 답변_삭제() throws Exception {
        DeleteHistory deleteHistory = savedAnswer.delete(loginUser);
        log.debug("{} :", deleteHistory);
        assertTrue(savedAnswer.isDeleted());
    }

    @Test(expected = CannotDeleteException.class)
    public void 답변_삭제_다른_유저() throws Exception {
        savedAnswer.delete(UserTest.SANJIGI);
        assertTrue(savedAnswer.isDeleted());
    }
}
