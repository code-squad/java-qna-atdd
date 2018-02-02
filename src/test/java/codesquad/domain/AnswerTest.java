package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AnswerTest {
	public static final User JAVAJIGI = new User(1L, "javajigi", "password", "name", "javajigi@slipp.net");
	public static final User SANJIGI = new User(2L, "sanjigi", "password", "name", "sanjigi@slipp.net");
	public Question question;
	public Answer answer;

	@Before
	public void setup() {
		question = new Question("질문제목", "질문내용");
		answer = new Answer("질문에 대한 답변");
	}

	@Test
	public void delete_owner() throws Exception {
		User writer = JAVAJIGI;

		question.writeBy(writer);
		answer.writeBy(writer);
		answer.setQuestion(question);
		answer.delete(writer);

		assertThat(answer.isDeleted(), is(true));
	}

	@Test(expected = CannotDeleteException.class)
	public void delete_not_owner() throws Exception {
		User writer = JAVAJIGI;
		User not_writer = SANJIGI;

		question.writeBy(writer);
		answer.writeBy(writer);
		answer.setQuestion(question);
		answer.delete(not_writer);
	}
}
