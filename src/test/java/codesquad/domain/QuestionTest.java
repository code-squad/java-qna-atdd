package codesquad.domain;

import codesquad.CannotDeleteException;
import codesquad.CannotUpdateException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class QuestionTest {
	public static final User SSOSSO = new User(1L, "ssosso", "password", "name", "ssossohow@gmail.com");
	
	public static final User QUINDICI = new User(2L, "quindici", "password", "name", "quindici@gmail.com");
	
	@Test
	public void add_answer() {
		Question question = new Question("제목", "내용");
		Answer answer = new Answer(SSOSSO, "답변");
		
		question.addAnswer(answer);
		assertThat(answer.getQuestion(), is(question));
	}
	
	@Test
	public void question_owner_check() {
		Question question = new Question("제목", "내용", SSOSSO);
		assertTrue(question.isOwner(SSOSSO));
		assertFalse(question.isOwner(QUINDICI));
	}
	
	@Test
	public void url_generate_check() {
		Question question = new Question("제목", "내용", SSOSSO);
		assertThat(question.generateUrl(), is("/questions/0"));
	}
	
	@Test
	public void update_owner() throws Exception {
		Question question = new Question("제목", "내용", SSOSSO);
		Question updateQuestion = new Question("제목1", "내용1");
		question.update(SSOSSO, updateQuestion);
		assertThat(question.getTitle(), is(updateQuestion.getTitle()));
		assertThat(question.getContents(), is(updateQuestion.getContents()));
	}
	
	@Test(expected = CannotUpdateException.class)
	public void update_not_owner() throws Exception {
		Question question = new Question("제목", "내용", SSOSSO);
		Question updateQuestion = new Question("제목1", "내용1");
		question.update(QUINDICI, updateQuestion);
	}
	
	@Test
	public void delete_owner() throws Exception {
		Question question = new Question("제목", "내용", SSOSSO);
		question.delete(SSOSSO);
		assertTrue(question.isDeleted());
	}
	
	@Test(expected = CannotDeleteException.class)
	public void delete_not_owner() throws Exception {
		Question question = new Question("제목", "내용", SSOSSO);
		question.delete(QUINDICI);
	}
	
	@Test
	public void delete_답변이없는경우() throws Exception {
		Question question = new Question("제목", "내용", SSOSSO);
		question.delete(SSOSSO);
		assertTrue(question.isDeleted());
	}
	
	@Test
	public void delete_질문자가작성한답변만있는경우() throws Exception {
		Question question = new Question("제목", "내용", SSOSSO);
		question.addAnswer(new Answer(SSOSSO, "내용"));
		question.addAnswer(new Answer(SSOSSO, "내용"));
		question.delete(SSOSSO);
		assertTrue(question.isDeleted());
		assertTrue(question.getAnswers().isAllDeleted());
	}
	
	@Test(expected = CannotDeleteException.class)
	public void delete_질문자가아닌다른사람이작성한답변이있는경우() throws Exception {
		Question question = new Question("제목", "내용", SSOSSO);
		question.addAnswer(new Answer(SSOSSO, "내용"));
		question.addAnswer(new Answer(QUINDICI, "내용"));
		question.delete(SSOSSO);
	}
}