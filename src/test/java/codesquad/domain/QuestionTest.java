package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import support.test.BaseTest;

import static org.junit.Assert.*;

public class QuestionTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(QuestionTest.class);

    public static User onwer = new User(1, "sehun", "test", "sehun", "test@test.com");
    public static User other = new User(2, "sechun", "test", "sechun", "test@test.com");

    public static Question question = new Question("title", "contents");
    public static Question updateQuestion = new Question("title2", "contents2");

    @Before
    public void setUp() throws Exception {
        question.writeBy(onwer);
        question.setId(1);
    }

    @Test
    public void update_owner() {
        question.update(updateQuestion, onwer);
        softly.assertThat(question.getTitle()).isEqualTo("title2");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_owner() {
        question.update(updateQuestion, other);
        softly.assertThat(question.getId()).isEqualTo(Long.valueOf(1));
    }

    @Test
    public void delete() {
        logger.debug("question : {}", question.isDeleted());
        question.delete();
        logger.debug("question : {}", question.isDeleted());
    }
}