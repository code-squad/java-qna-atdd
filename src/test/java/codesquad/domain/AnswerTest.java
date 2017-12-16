package codesquad.domain;

import static codesquad.domain.UserTest.newUser;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import codesquad.CannotDeleteException;


public class AnswerTest {
	private Answer answer;
    private User writer = newUser(1L);
    
    @Before
    public void setup() {
    	answer = new Answer(writer, "answer");
    }
    
    @Test
    public void delete_자신이_쓴_답변() throws Exception {
    	answer.delete(newUser(1L));
    	assertTrue(answer.isDeleted());
    }
	
	
    @Test(expected = CannotDeleteException.class)
    public void delete_다른_사람이_쓴_답변() throws Exception {
    	answer.delete(newUser(2L));
    }
}
