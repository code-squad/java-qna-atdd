package codesquad.domain;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswerTest {

    public static final Long DEFAULT_ANSWER_ID = 10L;
    public static final String DEFAULT_CONTENTS = "답변본문1";
    public static final User DEFAULT_WRITER = UserTest.JAVAJIGI;

    public static Answer defaultAnswer() {
        return new Answer(DEFAULT_ANSWER_ID, DEFAULT_WRITER, QuestionTest.FIRST_QUESTION, DEFAULT_CONTENTS);
    }

    @Test
    public void 생성() {
        Answer answer = new Answer(DEFAULT_WRITER, DEFAULT_CONTENTS);
    }

    @Test
    public void DTO변환() {
        Answer answer = defaultAnswer();
        AnswerDto dto = new AnswerDto(answer.getId(), answer.getContents(), answer.getWriter());

        final AnswerDto answerDto = answer.toAnswerDto();

        assertThat(answerDto).isEqualTo(dto);
        assertThat(answerDto.getId()).isEqualTo(DEFAULT_ANSWER_ID);
        assertThat(answerDto.getContents()).isEqualTo(DEFAULT_CONTENTS);
        assertThat(answerDto.getWriter()).isEqualTo(DEFAULT_WRITER.toUserDto());
    }

    @Test
    public void 삭제() {
        Answer answer = defaultAnswer();
        assertThat(answer.isDeleted()).isFalse();
        answer.delete(DEFAULT_WRITER);
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void 삭제_다른사용자() {
        Answer answer = defaultAnswer();
        answer.delete(UserTest.SANJIGI);
    }
}
