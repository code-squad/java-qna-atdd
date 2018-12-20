package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import org.junit.Test;
import org.mockito.Answers;
import org.springframework.test.util.ReflectionTestUtils;
import support.test.BaseTest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static codesquad.domain.AnswerTest.*;
import static codesquad.domain.UserTest.BRAD;
import static codesquad.domain.UserTest.JUNGHYUN;

public class QuestionTest extends BaseTest {
    public static final Question QUESTION = new Question(1L, "제목 테스트", "내용 테스트 - 코드스쿼드 qna-atdd step2 진행중입니다", BRAD);
    public static final Question QUESTION2 = new Question(2L, "제목 테스트2", "내용 테스트2 - 전혀 다른 내용입니다.", JUNGHYUN);
    public static final Question UPDATED_QUESTION = new Question("업데이트 제목", "업데이트 컨텐츠");

    @Test
    public void update_succeed() {
        Question newQuestion = QUESTION.update(BRAD, UPDATED_QUESTION);
        softly.assertThat(newQuestion.getTitle()).isEqualTo(UPDATED_QUESTION.getTitle());
        softly.assertThat(newQuestion.getContents()).isEqualTo(UPDATED_QUESTION.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_same_writer() {
        QUESTION.update(JUNGHYUN, UPDATED_QUESTION);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_same_writer() throws CannotDeleteException {
        QUESTION.delete(JUNGHYUN);
    }

    @Test
    public void delete_succeed() throws CannotDeleteException {
        QUESTION.delete(BRAD);
        softly.assertThat(QUESTION.isDeleted()).isEqualTo(true);
    }

    @Test
    public void 답변삭제_불가_다른유저답변() {
        List<Answer> answersForTest = new ArrayList(Arrays.asList(ANSWER2, ANSWER3));
        ReflectionTestUtils.setField(QUESTION, "answers", answersForTest);
        boolean result = ReflectionTestUtils.invokeMethod(QUESTION, "canDelete");
        softly.assertThat(result).isEqualTo(false);
    }

    @Test
    public void 답변삭제_가능_답변없음() {
        boolean result = ReflectionTestUtils.invokeMethod(QUESTION, "canDelete");
        softly.assertThat(result).isEqualTo(true);
    }

    @Test
    public void 답변삭제_가능_답변있음() {
        List<Answer> answersForTest = new ArrayList(Arrays.asList(ANSWER, ANSWER2));
        ReflectionTestUtils.setField(QUESTION, "answers", answersForTest);
        boolean result = ReflectionTestUtils.invokeMethod(QUESTION, "canDelete");
        softly.assertThat(result).isEqualTo(true);
    }

    @Test
    public void 답변삭제_프로세스() throws CannotDeleteException {
        QUESTION.processDeletion();
        softly.assertThat(QUESTION.isDeleted()).isTrue();
    }

}