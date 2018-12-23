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

    Question firstQuestion;
    Question secondQuestion;
    Question forUpdateQuestion;

    @Before
    public void setUp() throws Exception {
        firstQuestion = new Question("testTitle", "testContents");
        secondQuestion = new Question("secondTestTitle", "secondTestContents");
        forUpdateQuestion = new Question("modifyTitle", "modifyContents");
        firstQuestion.writeBy(CHOI);
        secondQuestion.writeBy(SING);
        forUpdateQuestion.writeBy(CHOI);
        QUESTION.writeBy(CHOI);
        QUESTION_FOR_UPDATE.writeBy(CHOI);
        QUESTION_FOR_UPDATE_OTHER_USER.writeBy(SING);
        QUESTION_FOR_DELETE.writeBy(CHOI);
    }

    @Test
    public void update() {
        firstQuestion.update(CHOI, forUpdateQuestion);
        softly.assertThat(firstQuestion.getTitle()).isEqualTo("modifyTitle");
        softly.assertThat(firstQuestion.getContents()).isEqualTo("modifyContents");
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateWithoutLogin() {
        firstQuestion.update(null, forUpdateQuestion);
    }

    @Test(expected = UnAuthorizedException.class)
    public void updateWithInvalidUser() {
        firstQuestion.update(SANJIGI, secondQuestion);
    }

    @Test
    public void delete() throws CannotDeleteException {
        secondQuestion.delete(SING);
        softly.assertThat(secondQuestion.isDeleted()).isTrue();
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteWithoutLogin() throws CannotDeleteException{
        secondQuestion.delete(null);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteWithInvalidUser() throws CannotDeleteException{
        secondQuestion.delete(CHOI);
    }
}
