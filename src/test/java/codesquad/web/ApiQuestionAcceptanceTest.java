package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

	public static final User SANJIGI = new User(2L, "sanjigi", "test", "name", "sanjigi@slipp.net");

	private QuestionDto createQuestionDto() {
		return new QuestionDto("질문제목", "질문내용");
	}

	@Test
	public void create() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		String location = createResourceBasicAuth("/api/questions", newQuestion);
		QuestionDto dbQuestion = getResource(location, QuestionDto.class);

		assertThat(dbQuestion, is(newQuestion));
	}

	@Test
	public void show() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		String location = createResourceBasicAuth("/api/questions", newQuestion);
		ResponseEntity<String> response = template().getForEntity(location, String.class);

		assertThat(response.getBody().contains(newQuestion.getTitle()), is(true));
	}

	@Test
	public void list() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		createResourceBasicAuth("/api/questions", newQuestion);

		QuestionsDto dbQuestions = getResource("/api/questions", QuestionsDto.class);
		assertThat(dbQuestions.getContents().contains(newQuestion), is(true));
	}

	@Test
	public void update() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		String location = createResourceBasicAuth("/api/questions", newQuestion);

		QuestionDto dbQuestion = getResource(location, QuestionDto.class);
		QuestionDto updateQuestion = new QuestionDto(dbQuestion.getId(), "바뀐 질문제목", "바뀐 질문제목");
		basicAuthTemplate().put(location, updateQuestion);

		dbQuestion = getResource(location, QuestionDto.class);
		assertThat(dbQuestion, is(updateQuestion));
	}

	@Test
	public void update_다른_사람() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		String location = createResource("/api/questions", newQuestion, basicAuthTemplate());

		QuestionDto dbQuestion = getResource(location, QuestionDto.class);
		QuestionDto updateQuestion = new QuestionDto(dbQuestion.getId(), "바뀐 질문제목", "바뀐 질문제목");
		basicAuthTemplate(SANJIGI).put(location, updateQuestion);

		dbQuestion = getResource(location, QuestionDto.class);
		assertThat(dbQuestion, is(newQuestion));
	}

	@Test
	public void delete() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		String location = createResourceBasicAuth("/api/questions", newQuestion);

		QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
		basicAuthTemplate().delete(location, dbQuestion);

		dbQuestion = getResource(location, QuestionDto.class);
		assertNull(dbQuestion);
	}

	@Test
	public void delete_no_exist() throws Exception {
		long question = 1;
		String location = String.format("/api/questions/%d", question);

		HtmlFormDataBuilder builder = HtmlFormDataBuilder.urlEncodedForm();
		ResponseEntity<String> response = basicAuthTemplate().postForEntity(location, builder.delete(), String.class);

		assertThat(response.getStatusCode(), Matchers.is(HttpStatus.PRECONDITION_REQUIRED));
	}

	@Test
	public void delete_다른_사람() throws Exception {
		QuestionDto newQuestion = createQuestionDto();
		String location = createResource("/api/questions", newQuestion, basicAuthTemplate());

		QuestionDto dbQuestion = template().getForObject(location, QuestionDto.class);
		basicAuthTemplate(SANJIGI).delete(location, dbQuestion);

		dbQuestion = getResource(location, QuestionDto.class);
		assertNotNull(dbQuestion);
	}

}
