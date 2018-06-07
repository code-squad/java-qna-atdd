package codesquad.domain;

import codesquad.exceptions.UnAuthorizedException;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AnswerTest {

    private Answer answer;
    private User owner;

    @Before
    public void setup() {
        owner = new User("larry", "test", "a", "b@b");
        answer = new Answer(owner, "content");
    }

    @Test
    public void update_success() {
        answer.update(owner, "change");
        assertThat(answer.getContents(), is("change"));
    }

    @Test (expected = UnAuthorizedException.class)
    public void update_fail() {
        User other = new User("sanjigi", "test", "a", "asdf@com");
        answer.update(other, "hey");
    }
}
