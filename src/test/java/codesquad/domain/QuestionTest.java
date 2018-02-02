package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QuestionTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionTest.class);

	public static final Question QUESTION1 = new Question("test1", "test1");
	public static final Question QUESTION2 = new Question("test2", "test2");
	public static final User USER1 = new User("test1", "test1", "test1", "test1");
	public static final User USER2 = new User("test2", "test2", "test2", "test2");

	public static Question newQuestion(Long id) {
		return new Question("title", "contents");
	}

	public static Question newQuestion(String title, String contents) {
		return new Question(title, contents);
	}

	@Test
	public void update_owner() throws Exception {
		Question origin = QUESTION1;
		origin.writeBy(USER1);

		Question target = new Question("우에엥", "집에가자아");
		origin.update(USER1, target);

		assertThat(origin.getTitle(), is(target.getTitle()));
		assertThat(origin.getContents(), is(target.getContents()));
	}

	@Test(expected = UnAuthorizedException.class)
	public void update_not_owner()  {
		Question origin = QUESTION1;
		origin.writeBy(USER1);

		Question target = new Question("우에엥", "집에가자아");
		origin.update(USER2, target);
	}

	@Test
	public void delete_owner() {
		Question question = QUESTION1;
		question.writeBy(USER1);

		question.delete(USER1);

		assertTrue(question.isDeleted());
	}

	@Test(expected = UnAuthorizedException.class)
	public void delete_not_owner() {
		Question question = QUESTION1;
		question.writeBy(USER1);

		question.delete(USER2);
	}
}
