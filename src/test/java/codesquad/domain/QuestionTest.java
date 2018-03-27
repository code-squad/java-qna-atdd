package codesquad.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codesquad.CannotDeleteException;
import codesquad.web.QuestionController;

public class QuestionTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	private User defaultUser() {
		return new User(0, "javajigi", "test1", "javajigi", "javajigiEmail@slipp.net");
	}
	
	private User anotherUser() {
		return new User(1, "sanjigi", "test2", "sanjigi", "sanjigiEmail@slipp.net");
	}
	
	private Question makeQuestion(User writer) {
		Question question = new Question("test", "test contents");
		question.writeBy(writer);
		
		return question;
	}
	
	@Test
	public void updateTest() {
		Question question = makeQuestion(defaultUser());
		question.update(anotherUser(), "update", "update contents");
		
		assertNotEquals(question.getTitle(), "update");
		assertNotEquals(question.getContents(), "update contents");
		assertEquals(question.getTitle(), "test");
		assertEquals(question.getContents(), "test contents");
	}
	
	@Test
	public void delete_본인삭제_답변없음() throws CannotDeleteException {
		Question question = makeQuestion(defaultUser());
//		List<DeleteHistory> histories = question.deleteQuestion(defaultUser());
		question.deleteQuestion(defaultUser());
		
		assertTrue(question.isDeleted());
	}
	
	@Test
	public void delete_본인삭제_답변글쓴이같음() throws CannotDeleteException {
		Question question = makeQuestion(defaultUser());
		Answer answer = new Answer(defaultUser(), "initial answer.");
		question.addAnswer(answer);
		
		question.deleteQuestion(defaultUser());
		assertTrue(question.isDeleted());
	}
	
	@Test
	public void delete_본인삭제_답변글쓴이다름() {
		Question question = makeQuestion(defaultUser());
		Answer answer = new Answer(anotherUser(), "initial answer.");
		question.addAnswer(answer);
		
		try {
			question.deleteQuestion(defaultUser());
		} catch (CannotDeleteException e) {
			log.debug(e.toString());
		}
		assertFalse(question.isDeleted());
	}
	
	@Test
	public void delete_타인삭제() {
		Question question = makeQuestion(defaultUser());
		try {
			question.deleteQuestion(anotherUser());
		} catch (CannotDeleteException e) {
			log.debug(e.toString());
		}
		assertFalse(question.isDeleted());
	}
}
