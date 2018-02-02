package codesquad.web;

import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

	@Autowired
	AnswerRepository answerRepository;

	private final AnswerDto answerDto = new AnswerDto("답변 컨텐츠다아아아아");

	@Test
	public void create() {
		String questionLocation = createQuestion();
		QuestionDto questionDto = getResource(questionLocation, QuestionDto.class);

		String location = createResource(questionLocation + "/answers", answerDto);

		AnswerDto dbAnswer = getResource(location, AnswerDto.class);
		assertThat(dbAnswer.getContents(), is(answerDto.getContents()));
	}

	@Test
	public void show() {
		String questionLocation = createQuestion();
		QuestionDto questionDto = getResource(questionLocation, QuestionDto.class);

		String location = createResource(questionLocation + "/answers", answerDto);

		ResponseEntity<String> response = template().getForEntity(location, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertTrue(response.getBody().contains(answerDto.getContents()));
	}

	@Test
	public void delete_owner() {
		String questionLocation = createQuestion();
		QuestionDto questionDto = getResource(questionLocation, QuestionDto.class);

		String location = createResource(questionLocation + "/answers", answerDto);
		basicAuthTemplate().delete(location);

		Answer answerDto = getResource(location, Answer.class);
		assertNull(answerDto);
	}

	@Test
	public void delete_not_owner() {
		String questionLocation = createQuestion();
		QuestionDto questionDto = getResource(questionLocation, QuestionDto.class);

		String location = createResource(questionLocation + "/answers", answerDto);
		User user = new User("hanna", "password", "한나", "jks7psj@gmail.com" );
		basicAuthTemplate(user).delete(location);

		Answer answerDto = getResource(location, Answer.class);
		assertNotNull(answerDto);
	}

	private String createQuestion() {
		QuestionDto question = new QuestionDto("질문이다아아아", "질문이다아아아아아");
		return createResource("/api/questions", question);
	}
}
