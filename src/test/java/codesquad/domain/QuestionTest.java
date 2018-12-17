package codesquad.domain;

import org.junit.Test;
import support.test.BaseTest;

public class QuestionTest extends BaseTest {
    /* 피드백6) 도메인 테스트코드 작성! */
    @Test
    public void applyOwnerTest() {
        softly.assertThat(QuestionFixture.testQuestion.applyOwner(UserFixture.testUser).isOwner()).isTrue();
    }
}
