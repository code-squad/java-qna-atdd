package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class QuestionTest {
    private Question originQuestion;

    @Before
    public void setup() {
        originQuestion = new Question(1, "title", "contents");
        originQuestion.writeBy(JAVAJIGI);
    }

    @Test
    public void update_question() {
        Question updateQuestion = new Question(1, "updateTitle", "updateContents");
        updateQuestion.writeBy(JAVAJIGI);
        originQuestion.update(updateQuestion);

        assertThat(updateQuestion.getTitle(), is(originQuestion.getTitle()));
        assertThat(updateQuestion.getContents(), is(originQuestion.getContents()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_question_wrong_writer() {
        Question updateQuestion = new Question(1, "updateTitle", "updateContents");
        updateQuestion.writeBy(SANJIGI);
        originQuestion.update(updateQuestion);
    }

    @Test
    public void update_question_wrong_id() {
        Question updateQuestion = new Question(2, "updateTitle", "updateContents");
        updateQuestion.writeBy(JAVAJIGI);
        originQuestion.update(updateQuestion);

        assertThat(updateQuestion.getTitle(), not(originQuestion.getTitle()));
        assertThat(updateQuestion.getContents(), not(originQuestion.getContents()));
    }
}
