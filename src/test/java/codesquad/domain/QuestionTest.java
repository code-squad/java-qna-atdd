package codesquad.domain;

import org.junit.Test;
import support.test.BaseTest;

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
}
