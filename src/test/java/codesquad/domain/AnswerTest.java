package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Test;
import org.slf4j.Logger;
import support.test.BaseTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static codesquad.domain.QuestionTest.QUESTION;
import static codesquad.domain.UserTest.BRAD;
import static codesquad.domain.UserTest.JUNGHYUN;
import static org.slf4j.LoggerFactory.getLogger;

public class AnswerTest extends BaseTest {
    private static final Logger log = getLogger(AnswerTest.class);

    public static final Answer ANSWER = new Answer(BRAD, "답변 내용 테스트1");
    public static final Answer ANSWER2 = new Answer(1L, BRAD, QUESTION, "답변 내용 테스트2");
    public static final Answer ANSWER3 = new Answer(2L, JUNGHYUN, QUESTION, "답변 내용 테스트3");
    public static final Answer ANSWER4 = new Answer(3L, BRAD, QUESTION, "답변 내용 테스트4");
    public static final Answer ANSWER5 = new Answer(4L, BRAD, QUESTION, "답변 내용 테스트5");
    public static final Answer ANSWER6 = new Answer(5L, BRAD, QUESTION, "답변 내용 테스트6");
    public static final Answer ANSWER7 = new Answer(6L, BRAD, QUESTION, "답변 내용 테스트7");

    @Test
    public void update() {
        Answer updatedAnswer = ANSWER.update(BRAD, "답변 내용 업데이트");
        softly.assertThat(updatedAnswer.isOwner(BRAD)).isEqualTo(true);
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_other_user() {
        ANSWER.update(JUNGHYUN, "답변 내용 업데이트");
    }

    @Test
    public void 삭제_성공() throws CannotDeleteException {
         DeleteHistory result = ANSWER.delete(BRAD);
         softly.assertThat(ANSWER.isDeleted()).isTrue();
         log.debug("answer deleteHistory : " + result);
    }

    @Test(expected = CannotDeleteException.class)
    public void 삭제_다른유저() throws CannotDeleteException {
        ANSWER.delete(JUNGHYUN);
    }

    @Test
    public void 질문삭제시_답변삭제() throws CannotDeleteException {
        List<Answer> answers = new ArrayList(Arrays.asList(ANSWER, ANSWER2));
        List<DeleteHistory> deleteHistories = new ArrayList<>();

        // 삭제 전
        softly.assertThat(ANSWER.isDeleted()).isFalse();
        softly.assertThat(ANSWER2.isDeleted()).isFalse();

        deleteHistories.addAll(Answer.delete(answers, BRAD));
        // 삭제 후
        softly.assertThat(ANSWER.isDeleted()).isTrue();
        softly.assertThat(ANSWER2.isDeleted()).isTrue();

        for (DeleteHistory deleteHistory : deleteHistories) {
            log.debug(deleteHistory.toString());
        }
    }

    @Test(expected = CannotDeleteException.class)
    public void 질문삭제시_다른유저답변_존재() throws CannotDeleteException {
        List<Answer> answers = new ArrayList(Arrays.asList(ANSWER, ANSWER3));
        Answer.delete(answers, BRAD);
    }

    @Test
    public void 질문삭제시_답변없을때() throws CannotDeleteException {
        List<Answer> answers = new ArrayList(Arrays.asList());
        Answer.delete(answers, BRAD);
    }
}