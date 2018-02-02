package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);

	public static final User SANJIGI = new User(2L, "sanjigi", "test", "name", "sanjigi@slipp.net");

	private QuestionDto createQuestionDto() {
		return new QuestionDto("질문제목", "질문내용");
	}
	private AnswerDto createAnswerDto() {
		return new AnswerDto("답변내용입니다.");
	}

	@Test
	public void create() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		AnswerDto newAnswer = createAnswerDto();

		String location = createQuestionAndAnswer(newQuestion, newAnswer);
		AnswerDto dbAnswer = getResource(location, AnswerDto.class);

		assertThat(dbAnswer, is(newAnswer));
	}

	@Test
	public void show() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		AnswerDto newAnswer = createAnswerDto();

		String location = createQuestionAndAnswer(newQuestion, newAnswer);
		ResponseEntity<String> response = template().getForEntity(location, String.class);

		assertThat(response.getBody().contains(newAnswer.getContents()), is(true));
	}

	@Test
	public void delete() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		AnswerDto newAnswer = createAnswerDto();

		String location = createQuestionAndAnswer(newQuestion, newAnswer);
		AnswerDto dbAnswer = getResource(location, AnswerDto.class);

		basicAuthTemplate().delete(location, dbAnswer);

		dbAnswer = getResource(location, AnswerDto.class);
		assertNull(dbAnswer);
	}

	@Test
	public void delete_다른_사람() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		AnswerDto newAnswer = createAnswerDto();

		String location = createQuestionAndAnswer(newQuestion, newAnswer);
		AnswerDto dbAnswer = getResource(location, AnswerDto.class);

		basicAuthTemplate(SANJIGI).delete(location, dbAnswer);

		dbAnswer = getResource(location, AnswerDto.class);
		assertNotNull(dbAnswer);
	}

	private String createQuestionAndAnswer(QuestionDto questionDto, AnswerDto answerDto) {
		String location = createResourceBasicAuth("/api/questions", questionDto);
		QuestionDto dbQuestion = getResource(location, QuestionDto.class);

		return createResourceBasicAuth(String.format("/api/questions/%d/answers", dbQuestion.getId()), answerDto);
	}

}
