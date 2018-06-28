package codesquad.domain;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DeleteHistoriesTest {
    @Test
    public void addHistory() {
        User learner = new User("learner", "password", "taewon", "email@naver.com");
        Question question = new Question("다윗은 어떻게 고난을 극복했는가?", "기도와 간구로");

        DeleteHistory history = new DeleteHistory(ContentType.QUESTION, 3L, learner, LocalDateTime.now());
        DeleteHistories histories = new DeleteHistories();

        assertThat(histories.size(), is(0));
        assertThat(histories.addHistory(history).size(), is(1));
    }
}
