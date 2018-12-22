package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    Question firstQuestion;
    Question secondQuestion;
    Question forUpdateQuestion;

    User choi = new User(1, "choi", "1234", "choi", "chltmdals115@gmail.com");
    User sing = new User(2, "sing", "1234", "sing", "sing@gmail.com");

    @Before
    public void setUp() throws Exception {
        firstQuestion = new Question("testTitle", "testContents");
        secondQuestion = new Question("secondTestTitle", "secondTestContents");
        forUpdateQuestion = new Question("modifyTitle", "modifyContents");
        firstQuestion.writeBy(choi);
        secondQuestion.writeBy(sing);
        forUpdateQuestion.writeBy(choi);
    }

    @Test
    public void update() {
        firstQuestion.update(choi, forUpdateQuestion);
        softly.assertThat(firstQuestion.getTitle()).isEqualTo("modifyTitle");
        softly.assertThat(firstQuestion.getContents()).isEqualTo("modifyContents");
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateWithoutLogin() {
        firstQuestion.update(null, forUpdateQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateWithInvalidUser() {
        firstQuestion.update(choi, secondQuestion);
    }

    @Test
    public void delete() throws CannotDeleteException {
        secondQuestion.delete(sing);
        softly.assertThat(secondQuestion.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteWithoutLogin() throws CannotDeleteException{
        secondQuestion.delete(null);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteWithInvalidUser() throws CannotDeleteException{
        secondQuestion.delete(choi);
    }
}
