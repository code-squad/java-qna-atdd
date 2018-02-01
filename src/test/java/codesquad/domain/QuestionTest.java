package codesquad.domain;

import codesquad.UnAuthorizedException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {
	public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
	public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
	public static final Question QUESTION_1 = new Question("질문1", "질문내용1");
	public static final Question QUESTION_2 = new Question("질문2", "질문내용2");

	public static Question newQuestion(String title) {
		return newQuestion(title, "contents");
	}

	public static Question newQuestion(String title, String contents) {
		return new Question(title, contents);
	}

	@Test
	public void update_owner() throws Exception {
		User writer = JAVAJIGI;

		Question origin = newQuestion(QUESTION_1.getTitle(), QUESTION_1.getContents());
		origin.writeBy(writer);

		Question target = newQuestion(QUESTION_2.getTitle(), QUESTION_2.getContents());
		origin.update(writer, target);

		assertThat(origin.getTitle(), is(target.getTitle()));
		assertThat(origin.getContents(), is(target.getContents()));
	}

	@Test(expected = UnAuthorizedException.class)
	public void update_not_owner() {
		User writer = JAVAJIGI;
		User not_writer = SANJIGI;

		Question origin = newQuestion(QUESTION_1.getTitle(), QUESTION_1.getContents());
		origin.writeBy(writer);

		Question target = newQuestion(QUESTION_2.getTitle(), QUESTION_2.getContents());
		origin.update(not_writer, target);
	}

	@Test
	public void delete_owner() throws Exception {
		User writer = JAVAJIGI;

		Question origin = newQuestion(QUESTION_1.getTitle(), QUESTION_1.getContents());
		origin.writeBy(writer);
		origin.delete(writer);

		assertThat(origin.isDeleted(), is(true));
	}

	@Test(expected = UnAuthorizedException.class)
	public void delete_not_owner() {
		User writer = JAVAJIGI;
		User not_writer = SANJIGI;

		Question origin = newQuestion(QUESTION_1.getTitle(), QUESTION_1.getContents());
		origin.writeBy(writer);
		origin.delete(not_writer);
	}
}
