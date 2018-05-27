package codesquad.domain;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;

import org.junit.Before;
import org.junit.Test;

import codesquad.CannotDeleteException;

public class QuestionTest {

	private Question question;
	private User user;
	private User otherUser;

	@Before
	public void setUp() {
		user = new User(1, "javajigi", "비밀번호", "이름", "test@mail");
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

	@Test
	public void delete본인() throws AuthenticationException, CannotDeleteException{
		List<DeleteHistory> histories = question.delete(user);
		assertThat(question.isDeleted(), is(true));
		List<DeleteHistory> testHistories = new ArrayList<>();
		testHistories.add(new DeleteHistory(ContentType.QUESTION, 0L, user));
		assertThat(histories, is(testHistories));
		
	}
	
	@Test(expected=AuthenticationException.class)
	public void delete다른사람() throws AuthenticationException, CannotDeleteException{
		question.delete(otherUser);
	}

	@Test
	public void delete_댓글존재_모두질문유저꺼() throws AuthenticationException, CannotDeleteException{
		Answer answer = new Answer(1L, user, question, "내용내용");
		question.addAnswer(answer);
		List<DeleteHistory> histories = question.delete(user);
		assertThat(question.isDeleted(), is(true));
		assertThat(answer.isDeleted(), is(true));
		
		List<DeleteHistory> testHistories = new ArrayList<>();
		testHistories.add(new DeleteHistory(ContentType.ANSWER, 1L, user));
		testHistories.add(new DeleteHistory(ContentType.QUESTION, 0L, user));
		assertThat(histories, is(testHistories));
	}
	
	@Test(expected=CannotDeleteException.class)
	public void delete_댓글존재_다른유저꺼() throws AuthenticationException, CannotDeleteException{
		Answer answer = new Answer(1L, otherUser, question, "내용내용");
		question.addAnswer(answer);
		question.delete(user);
	}
	
	@Test
	public void checkAnswerStatus_다른유저댓글존재() {
		question.addAnswer(new Answer(1L, user, question, "내용내용"));
		question.addAnswer(new Answer(2L, otherUser, question, "내용내용"));
		assertThat(question.checkAnswerStatus(user), is(false));
	}
	@Test
	public void checkAnswerStatus() {
		question.addAnswer(new Answer(1L, user, question, "내용내용"));
		question.addAnswer(new Answer(1L, user, question, "내용내용"));
		assertThat(question.checkAnswerStatus(user), is(true));
	}
	
	@Test(expected=AuthenticationException.class)
	public void checkQuestionStatus_다른유저() throws AuthenticationException, CannotDeleteException {
		question.checkQuestionStatus(otherUser);
	}

	@Test(expected=CannotDeleteException.class)
	public void checkQuestionStatus_이미지워진글() throws AuthenticationException, CannotDeleteException {
		question.delete(user);
		question.checkQuestionStatus(user);
	}
	
	@Test
	public void deleteAnswer() throws AuthenticationException, CannotDeleteException {
		question.addAnswer(new Answer(1L, user, question, "내용내용"));
		List<DeleteHistory> histories = new ArrayList<>();
		histories.add(new DeleteHistory(ContentType.ANSWER, 1L, user));
		assertThat(question.deleteAnswer(user), is(histories));
	}
	
}
