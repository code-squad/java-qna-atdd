package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import org.springframework.test.context.TestPropertySource;
import support.test.BaseTest;
import java.util.*;

public class QuestionTest extends BaseTest {
    /* 피드백6) 도메인 테스트코드 작성! */
    @Test
    public void applyOwnerTest() {
        softly.assertThat(QuestionFixture.TEST_QUESTION.applyOwner(UserFixture.TEST_USER).isOwner()).isTrue();
    }

    @Test
    public void question_일치유무() {
        softly.assertThat(new Question("title", "contents", UserFixture.JAVAJIGI_USER)
                .isTitleAndContentsAndWriter(new Question("title", "contents", UserFixture.JAVAJIGI_USER)))
                .isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestionTest() {
        Question question = QuestionFixture.TEST_QUESTION;
        question.addAnswer(AnswerFixture.TEST_ANSWER);
        question.addAnswer(AnswerFixture.TEST_ANSWER_SANJIGI);
        question.deleteQuestion(UserFixture.JAVAJIGI_USER);
    }
}
