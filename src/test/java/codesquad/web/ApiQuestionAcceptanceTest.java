package codesquad.web;

import codesquad.domain.QuestionRepository;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.test.AcceptanceTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
	
	private static QuestionDto defaultQuestionDto;
	private static AnswerDto defaultAnswerDto;
	
	@Autowired
	private QuestionRepository questionRepository;
	
	private String createQuestion() {
		return createResource(ApiQuestionController.BASE_URL, defaultQuestionDto, defaultUser());
	}
	
	private String createAnswer(long questionId) {
		return createResource(ApiQuestionController.BASE_URL + "/" + questionId + "/answers", defaultAnswerDto, defaultUser());
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		defaultQuestionDto = new QuestionDto("제목테스트", "내용테스트");
		defaultAnswerDto = new AnswerDto("내용테스트");
	}
	
	@Test
	public void create() {
		String location = createQuestion();
		QuestionDto saveQuestion = getResource(location, QuestionDto.class);

		assertThat(saveQuestion.getTitle(), is(defaultQuestionDto.getTitle()));
		assertThat(saveQuestion.getContents(), is(defaultQuestionDto.getContents()));
	}
	
	@Test
	public void list() {
		IntStream.range(0, 12)
				.forEach(n -> createQuestion());
		
		assertThat(getResource(ApiQuestionController.BASE_URL, List.class).size(), is(10));
	}
	
	@Test
	public void update_다른_사람() throws Exception{
		String location = createQuestion();
		QuestionDto updateQuestion = new QuestionDto("제목테스트2", "내용테스트2");
		basicAuthTemplate(newUser("ssosso1")).put(location, updateQuestion);

		QuestionDto saveQuestion = getResource(location, QuestionDto.class);
		assertThat(saveQuestion.getTitle(), is(defaultQuestionDto.getTitle()));
		assertThat(saveQuestion.getContents(), is(defaultQuestionDto.getContents()));
	}

	@Test
	public void update() throws Exception{
		String location = createQuestion();
		QuestionDto updateQuestion = new QuestionDto("제목테스트2", "내용테스트2");
		basicAuthTemplate(defaultUser()).put(location, updateQuestion);

		QuestionDto saveQuestion = getResource(location, QuestionDto.class);
		assertThat(saveQuestion.getTitle(), is(updateQuestion.getTitle()));
		assertThat(saveQuestion.getContents(), is(updateQuestion.getContents()));
	}

	@Test
	public void delete_다른_사람() throws Exception{
		String location = createQuestion();
		basicAuthTemplate(newUser("ssosso2")).delete(location);
		
		long deleteId = Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
		assertThat(questionRepository.findOne(deleteId).get().isDeleted(), is(false));
	}

	@Test
	public void delete() throws Exception{
		String location = createQuestion();
		basicAuthTemplate(defaultUser()).delete(location);

		long deleteId = Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
		assertThat(questionRepository.findOne(deleteId).get().isDeleted(), is(true));
	}
	
	@Test
	public void addAnswer() {
		long questionId = getResource(createQuestion(), QuestionDto.class, defaultUser()).getId();
		String location = createAnswer(questionId);
		
		assertThat(getResource(location, AnswerDto.class).getContents(), is(defaultAnswerDto.getContents()));
	}
	
	@Test
	public void listAnswer() {
		long questionId = getResource(createQuestion(), QuestionDto.class, defaultUser()).getId();
		IntStream.range(0, 5)
				.forEach(n -> createAnswer(questionId));
		
		assertThat(getResource(ApiQuestionController.BASE_URL + "/" + questionId + "/answers", List.class).size(), is(5));
	}
}
