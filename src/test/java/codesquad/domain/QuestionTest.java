package codesquad.domain;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.naming.AuthenticationException;

import org.junit.Before;
import org.junit.Test;

public class QuestionTest {

	private Question question;
	private User user;
	private User otherUser;

	@Before
	public void setUp() {
		user = new User(1, "그램", "비밀번호", "이름", "test@mail");
		otherUser = new User(2, "두램", "비밀번호", "이름", "test@mail");
		question = new Question("제목", "내용");
		question.writeBy(user);
	}

	@Test(expected = AuthenticationException.class)
	public void updateFail() throws AuthenticationException {
		Question updatedQuestion = new Question("수정", "수정");
		question.update(updatedQuestion, otherUser);
	}

	@Test
	public void updateSuccess() throws AuthenticationException {
		Question updatedQuestion = new Question("수정", "수정");
		question.update(updatedQuestion, user);
		assertThat(question, is(updatedQuestion));
	}

}
