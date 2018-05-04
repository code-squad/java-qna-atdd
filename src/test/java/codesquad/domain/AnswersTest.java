package codesquad.domain;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnswersTest {
	public static final User SSOSSO = new User(1L, "ssosso", "password", "name", "ssossohow@gmail.com");
	
	public static final User QUINDICI = new User(2L, "quindici", "password", "name", "quindici@gmail.com");
	
	private long answerIndex;
	
	private Question newQuestion(User writer) {
		return new Question("제목", "내용", writer);
	}
	
	private Answer newAnswer(Question question) {
		return new Answer(++answerIndex, SSOSSO, question, "답변");
	}
	
	private Answer newAnswer() {
		return new Answer(SSOSSO, "답변");
	}
	
	@Test
	public void deleteAll() throws Exception {
		Answer answer1 = newAnswer();
		Answer answer2 = newAnswer();
		Answer answer3 = newAnswer();
		
		Answers answers = new Answers(Arrays.asList(answer1, answer2, answer3));
		answers.deleteAll(SSOSSO);
		assertTrue(answers.isAllDeleted());
	}
	
	@Test
	public void isAllDeleted() throws Exception {
		Answer answer1 = newAnswer();
		Answer answer2 = newAnswer();
		Answer answer3 = newAnswer();
		answer1.delete(SSOSSO);
		
		Answers answers = new Answers(Arrays.asList(answer1, answer2, answer3));
		assertFalse(answers.isAllDeleted());
		
		answer2.delete(SSOSSO);
		answer3.delete(SSOSSO);
		answers = new Answers(Arrays.asList(answer1, answer2, answer3));
		assertTrue(answers.isAllDeleted());
	}
	
	@Test
	public void equalAllAnswerWriterWithQuestionWriter() {
		Answer answer1 = newAnswer(newQuestion(SSOSSO));
		Answer answer2 = newAnswer(newQuestion(SSOSSO));
		Answer answer3 = newAnswer(newQuestion(QUINDICI));
		
		Answers answers = new Answers(Arrays.asList(answer1, answer2, answer3));
		assertFalse(answers.equalAllAnswerWriterWithQuestionWriter());
		
		answers = new Answers(Arrays.asList(answer1, answer2));
		assertTrue(answers.equalAllAnswerWriterWithQuestionWriter());
	}
	
	@Test
	public void getPageRequest() {
		Answers answers = new Answers(LongStream.range(0, 10)
				.mapToObj(n -> new Answer(n, SSOSSO, newQuestion(SSOSSO), "답변"))
				.collect(Collectors.toList()));
		AtomicLong index = new AtomicLong(5);
		assertTrue(answers.getPageRequest(1, 5)
				.stream()
				.map(answer -> answer.getId() == index.getAndIncrement())
				.reduce((aBoolean, aBoolean2) -> aBoolean && aBoolean2)
				.get());
	}
}
