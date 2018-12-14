package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(QuestionTest.class);

    static User owner = new User(1, "sehun", "test", "sehun", "test@test.com");
    static User other = new User(2, "sechun", "test", "sechun", "test@test.com");

    static Question question = new Question("title", "contents");
    static Question question2 = new Question("title", "contents");
    static Question updateQuestion = new Question("title2", "contents2");

    static Answer answer1 = new Answer(1L, owner, question, "contents1");
    static Answer answer2 = new Answer(2L, other, question2, "content2");

    @Before
    public void setUp() throws Exception {
        question.writeBy(owner);
        question.setId(1L);
        question2.writeBy(other);
        question2.setId(2L);
    }

    @Test
    public void update_owner() {
        question.update(updateQuestion, owner);
        softly.assertThat(question.getTitle()).isEqualTo("title2");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        question.update(updateQuestion, other);
        softly.assertThat(question.getId()).isEqualTo(Long.valueOf(1));
    }

    @Test
    public void delete() throws CannotDeleteException {
        question.addAnswer(answer1);
        for (DeleteHistory deleteHistory : question.delete(owner)) {
            logger.debug("deleteHistory : {}", deleteHistory);
        }
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_by_other() throws CannotDeleteException {
        question.addAnswer(answer1);
        question.delete(other);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_cannot_deleteAnswer() throws CannotDeleteException {
        question.addAnswer(answer2);
        question.delete(owner);
    }
}