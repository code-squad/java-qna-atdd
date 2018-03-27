package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	@Resource
	private QuestionRepository questionRepository;
	
	@Test
	public void create() {
		QuestionDto newQuestion = createQuestionDto("Test title");
		String location = createResource("/api/questions", newQuestion);
		QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
		assertThat(dbQuestion.getId(), is(newQuestion.getId()));
		assertThat(dbQuestion.getTitle(), is(newQuestion.getTitle()));
		assertThat(dbQuestion.getContents(), is(newQuestion.getContents()));
	}

	@Test
	public void show() {
		QuestionDto newQuestion = createQuestionDto("Test title");
		String location = createResource("/api/questions", newQuestion);
		ResponseEntity<String> response = basicAuthTemplate(defaultUser()).getForEntity(location, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	private QuestionDto createQuestionDto(String title) {
		QuestionDto question = new QuestionDto(title, "This is a test question contents.");
		return question;
	}

	private QuestionDto createUpdatedQuestionDto(String title) {
		QuestionDto question = new QuestionDto(title, "This is a updated question contents.");
		return question;
	}

	@Test
	public void update() {
		QuestionDto newQuestion = createQuestionDto("Test title.");
		String location = createResource("/api/questions", newQuestion);
		QuestionDto updateQuestion = createUpdatedQuestionDto("Update title.");

		basicAuthTemplate().put(location, updateQuestion);

		QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());

		assertThat(dbQuestion.getId(), is(updateQuestion.getId()));
		assertThat(dbQuestion.getTitle(), is(updateQuestion.getTitle()));
		assertThat(dbQuestion.getContents(), is(updateQuestion.getContents()));
	}

	@Test
	public void update_다른_사람() {
		QuestionDto newQuestion = createQuestionDto("Test title.");
		String location = createResource("/api/questions", newQuestion);
		
		QuestionDto updateQuestion = createUpdatedQuestionDto("Update title.");
		User wrongUser = new User("koo", "koosangyoon", "test", "koo@naver.com");
		basicAuthTemplate(wrongUser).put(location, updateQuestion);

		QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());

		assertThat(dbQuestion.getId(), is(newQuestion.getId()));
		assertThat(dbQuestion.getTitle(), is(newQuestion.getTitle()));
		assertThat(dbQuestion.getContents(), is(newQuestion.getContents()));
	}
	
	@Test
	public void delete_본인삭제_답변없음() {
		QuestionDto newQuestion = createQuestionDto("Test title.");
		String location = createResource("/api/questions", newQuestion);
		
		basicAuthTemplate().delete(location);
		
		long questionId = Long.parseLong(location.charAt(location.length() - 1) + "");
		
		Question question = questionRepository.findOne((long) questionId);
		log.debug("question delete status is " + question.isDeleted());
		assertTrue(question.isDeleted());
	}
	
	@Test
	public void delete_본인삭제_답변글쓴이같음() {
		QuestionDto newQuestion = createQuestionDto("Test title");
		String questionLocation = createResource("/api/questions", newQuestion);

		String answerLocation = questionLocation + "/answers";

		Answer answer = new Answer((long) 1, defaultUser(), newQuestion.toQuestion(), "initial answer contents");

		ResponseEntity<String> response = basicAuthTemplate(defaultUser()).postForEntity(answerLocation, answer, String.class);
		
		basicAuthTemplate(defaultUser()).delete(questionLocation);
		
		long questionId = Long.parseLong(questionLocation.charAt(questionLocation.length() - 1) + "");
		Question question = questionRepository.findOne((long) questionId);
		log.debug("question delete status is " + question.isDeleted());
		assertTrue(question.isDeleted());
	}
	
	@Test
	public void delete_본인삭제_답변글쓴이다름() {
		QuestionDto newQuestion = createQuestionDto("Test title");
		String questionLocation = createResource("/api/questions", newQuestion);
		
		String answerLocation = questionLocation + "/answers";
		
		Answer answer = new Answer((long) 1, anotherUser(), newQuestion.toQuestion(), "initial answer contents");
		
		ResponseEntity<String> response = basicAuthTemplate(anotherUser()).postForEntity(answerLocation, answer, String.class);
		
		basicAuthTemplate(defaultUser()).delete(questionLocation);
		
		long questionId = Long.parseLong(questionLocation.charAt(questionLocation.length() - 1) + "");
		Question question = questionRepository.findOne((long) questionId);
		log.debug("question delete status is " + question.isDeleted());
		assertFalse(question.isDeleted());
	}
	
	@Test
	public void delete_but_no_question() {
		basicAuthTemplate().delete("/api/questions/1");
	}
	
	@Test
	public void delete_타인삭제() {
		QuestionDto newQuestion = createQuestionDto("Test title.");
		String location = createResource("/api/questions", newQuestion);
		basicAuthTemplate(anotherUser()).delete(location);
		
		long questionId = Long.parseLong(location.charAt(location.length() - 1) + "");
		Question question = questionRepository.findOne((long) questionId);
		log.debug("question delete status is " + question.isDeleted());
		assertFalse(question.isDeleted());
	}
}
