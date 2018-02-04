package codesquad.domain;

import codesquad.CannotDeleteException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AnswersTest {

    private User javajigi;

    private Answers answers;

    @Before
    public void setUp() throws Exception {
        javajigi = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");

        answers = new Answers();
        answers.addAnswer(new Answer(javajigi, "테스트 답변 1"));
        answers.addAnswer(new Answer(javajigi, "테스트 답변 2"));
    }

    @Test
    public void canDeleteAnswers_질문이존재하지않는경우() throws Exception {
        Answers answers = new Answers();
        assertThat(answers.canDeleteAllAnswers(javajigi)).isTrue();
    }

    @Test
    public void canDeleteAnswers_로그인한사람이답변의글쓴이인경우() throws Exception {
        assertThat(answers.canDeleteAllAnswers(javajigi)).isTrue();
    }

    @Test
    public void canDeleteAnswers_로그인한사람이답변의글쓴이가아닌경우() throws Exception {
        User gunju = new User(3, "gunju", "test", "고건주", "gunju@slipp.net");
        answers.addAnswer(new Answer(gunju, "테스트"));
        assertThat(answers.canDeleteAllAnswers(javajigi)).isFalse();
    }

    @Test
    public void deleteAll_질문이존재하지않는경우() throws Exception {
        Answers answers = new Answers();
        List<DeleteHistory> histories = answers.deleteAll(javajigi);

        assertThat(histories.isEmpty()).isTrue();
    }

    @Test
    public void deleteAll_로그인한사람이답변의글쓴이인경우() throws Exception {
        List<DeleteHistory> histories = answers.deleteAll(javajigi);

        assertThat(histories.size()).isEqualTo(2);
        assertThat(answers.getCountOfAnswers()).isEqualTo(0);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteAll_로그인한사람이답변의글쓴이가아닌경우() throws Exception {
        User gunju = new User(3, "gunju", "test", "고건주", "gunju@slipp.net");
        answers.addAnswer(new Answer(gunju, "테스트"));

        answers.deleteAll(javajigi);
    }

}