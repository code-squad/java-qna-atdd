package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class QuestionTest {
	public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
	public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
	public Question origin;
	public QuestionDto target;
	public Answer answer;

	@Before
	public void setup() {
		origin = new Question("질문1", "질문내용1");
		target = new QuestionDto("질문2", "질문내용2");
		answer = new Answer("질문에 대한 답변");
	}

	@Test
	public void update_owner() throws Exception {
		User writer = JAVAJIGI;
		origin.writeBy(writer);

		origin.update(writer, target);

		assertThat(origin.getTitle(), is(target.getTitle()));
		assertThat(origin.getContents(), is(target.getContents()));
	}

	@Test(expected = UnAuthorizedException.class)
	public void update_not_owner() {
		User writer = JAVAJIGI;
		User not_writer = SANJIGI;

		origin.writeBy(writer);

		origin.update(not_writer, target);
	}

	@Test
	public void delete_owner() throws Exception {
		User writer = JAVAJIGI;

		origin.writeBy(writer);
		origin.delete(writer);

		assertThat(origin.isDeleted(), is(true));
	}

	@Test(expected = CannotDeleteException.class)
	public void delete_not_owner() throws Exception {
		User writer = JAVAJIGI;
		User not_writer = SANJIGI;

		origin.writeBy(writer);
		origin.delete(not_writer);
	}

	@Test
	public void delete_내_답글이_있을_때() throws Exception {
		User writer = JAVAJIGI;

		origin.writeBy(writer);

		answer.writeBy(writer);
		answer.setQuestion(origin);
		origin.addAnswer(answer);

		origin.delete(writer);

		assertThat(origin.isDeleted(), is(true));
	}

	@Test(expected = CannotDeleteException.class)
	public void delete_남의_답글이_있을_때() throws Exception {
		User writer = JAVAJIGI;
		User not_writer = SANJIGI;

		origin.writeBy(writer);

		answer.writeBy(not_writer);
		answer.setQuestion(origin);
		origin.addAnswer(answer);

		origin.delete(writer);
	}

}
