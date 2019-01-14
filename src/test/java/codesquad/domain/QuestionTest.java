package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import support.test.BaseTest;

import static codesquad.domain.UserTest.CHOI;
import static codesquad.domain.UserTest.SANJIGI;
import static codesquad.domain.UserTest.SING;

public class QuestionTest extends BaseTest {
    public static final Question QUESTION = new Question("baseTitle", "baseContents");
    public static final Question QUESTION_FOR_UPDATE = new Question("updateTitle", "updateContents");
    public static final Question QUESTION_FOR_UPDATE_OTHER_USER = new Question("otherTitle", "otherContents");
    public static final Question QUESTION_FOR_DELETE = new Question("delTitle", "delContents");

    private Question firstQuestion = new Question("testTitle", "testContents");
    private Question secondQuestion = new Question("secondTestTitle", "secondTestContents");
    private Question forUpdateQuestion = new Question("modifyTitle", "modifyContents");

    @Test
    public void update() {
        firstQuestion.writeBy(CHOI);
        firstQuestion.update(CHOI, forUpdateQuestion);
        softly.assertThat(firstQuestion.getTitle()).isEqualTo("modifyTitle");
        softly.assertThat(firstQuestion.getContents()).isEqualTo("modifyContents");
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateWithoutLogin() {
        firstQuestion.writeBy(CHOI);
        firstQuestion.update(null, forUpdateQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateWithInvalidUser() {
        firstQuestion.writeBy(CHOI);
        firstQuestion.update(SANJIGI, secondQuestion);
    }

    @Test
    public void delete() throws CannotDeleteException {
        secondQuestion.writeBy(SING);
        secondQuestion.delete(SING);
        softly.assertThat(secondQuestion.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteWithoutLogin() throws CannotDeleteException{
        secondQuestion.writeBy(SING);
        secondQuestion.delete(null);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteWithInvalidUser() throws CannotDeleteException{
        secondQuestion.writeBy(SING);
        secondQuestion.delete(CHOI);
    }
}
