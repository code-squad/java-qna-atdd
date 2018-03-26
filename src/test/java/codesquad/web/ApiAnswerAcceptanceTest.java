package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	@Resource
	private QuestionRepository questionRepository;

	private QuestionDto createQuestionDto(String title) {
		QuestionDto question = new QuestionDto(title, "This is a test question contents.");
		return question;
	}

	@Test
	public void create_로그인한사용자() {
		QuestionDto newQuestion = createQuestionDto("Test title");
		String questionLocation = createResource("/api/questions", newQuestion);

		String answerLocation = questionLocation + "/answers";

		Answer answer = new Answer((long) 1, defaultUser(), newQuestion.toQuestion(), "initial answer contents");

		ResponseEntity<String> response = basicAuthTemplate().postForEntity(answerLocation, answer, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
	}

	@Test
	public void create_게스트_작성불가() {
		QuestionDto newQuestion = createQuestionDto("Test title");
		String questionLocation = createResource("/api/questions", newQuestion);

		String answerLocation = questionLocation + "/answers";
		String contents = "initial answer.";

		ResponseEntity<String> response = template().postForEntity(answerLocation, contents, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void update_본인수정() {
		String updateLocation = makeQuestionAnswer(2) + "/1";
		String updatedContents = "update answer.";
		
		basicAuthTemplate().put(updateLocation, updatedContents);
		
		ResponseEntity<Answer> dbAnswer = basicAuthTemplate(defaultUser()).getForEntity(updateLocation, Answer.class);
		assertEquals(updatedContents, dbAnswer.getBody().getContents());
	}
	
	@Test
	public void update_타인수정() {
		String updateLocation = makeQuestionAnswer(3) + "/1";
		String updatedContents = "update answer.";
		
		template().put(updateLocation, updatedContents);
		
		ResponseEntity<Answer> dbAnswer = basicAuthTemplate().getForEntity(updateLocation, Answer.class);
		log.debug("**" + dbAnswer.getBody().getContents());
		assertNotEquals(updatedContents, dbAnswer.getBody().getContents());
		assertEquals("initial answer contents", dbAnswer.getBody().getContents());
	}
	
	private String makeQuestionAnswer(long id) {
		QuestionDto newQuestion = createQuestionDto("Test title");
		String questionLocation = createResource("/api/questions", newQuestion);

		String answerLocation = questionLocation + "/answers";

		Answer answer = new Answer(id, defaultUser(), newQuestion.toQuestion(), "initial answer contents");
		
		ResponseEntity<String> makeAnswerResponse = basicAuthTemplate().postForEntity(answerLocation, answer, String.class);
		assertThat(makeAnswerResponse.getStatusCode(), is(HttpStatus.CREATED));
		return answerLocation;
	}
	
	@Test
	public void delete_본인삭제() {
		String deleteLocation = makeQuestionAnswer(4) + "/1";
		ResponseEntity<Answer> dbAnswer = basicAuthTemplate().postForEntity(deleteLocation, null, Answer.class);
		assertTrue(dbAnswer.getBody().isDeleted());
	}
	
	@Test
	public void delete_타인삭제() {
		String deleteLocation = makeQuestionAnswer(4) + "/1";
		ResponseEntity<String> response = template().postForEntity(deleteLocation, null, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}
}
