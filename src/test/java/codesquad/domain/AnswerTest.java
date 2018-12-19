package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import static codesquad.domain.QuestionTest.QNA1;
import static codesquad.domain.UserTest.JAVAJIGI;
import static codesquad.domain.UserTest.SANJIGI;

public class AnswerTest extends BaseTest {
    private static final Answer ANSWER1 = new Answer(1L,JAVAJIGI,QNA1,"1번질문");
    private static final Answer ANSWER2 = new Answer(2L,JAVAJIGI,QNA1,"2번질문");

    public static Answer newAnswer(String contents) {
        return new Answer(JAVAJIGI, contents);
    }


    @Test
    public void update() {
        Answer answer = newAnswer("hhh");
        answer.update(JAVAJIGI,"ggg");
        softly.assertThat(answer.getContents()).isEqualTo("ggg");
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_no() {
        Answer answer = newAnswer("hhh");
        answer.update(SANJIGI,"ggg");
    }

    @Test
    public void to_question() {
        Answer answer = newAnswer("질문 내용");
        answer.toQuestion(QNA1);
        softly.assertThat(answer.isOwner(JAVAJIGI)).isTrue();
        softly.assertThat(answer.getQuestion()).isEqualTo(QNA1);
    }

}
