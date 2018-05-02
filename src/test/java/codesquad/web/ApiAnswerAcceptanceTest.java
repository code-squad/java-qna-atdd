package codesquad.web;

import codesquad.domain.AnswerRepository;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import support.test.AcceptanceTest;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {
	
	private static QuestionDto defaultQuestionDto;
	private static AnswerDto defaultAnswerDto;
	
	@Autowired
	private AnswerRepository answerRepository;
	
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
	public void update_다른_사람() throws Exception{
		long questionId = getResource(createQuestion(), QuestionDto.class, defaultUser()).getId();
		String location = createAnswer(questionId);
		
		AnswerDto updateAnswer = new AnswerDto("답변테스트2");
		basicAuthTemplate(newUser("ssosso3")).put(location, updateAnswer);
		
		AnswerDto saveAnswer = getResource(location, AnswerDto.class);
		assertThat(saveAnswer.getContents(), is(defaultAnswerDto.getContents()));
	}
	
	@Test
	public void update() throws Exception{
		long questionId = getResource(createQuestion(), QuestionDto.class, defaultUser()).getId();
		String location = createAnswer(questionId);
		
		AnswerDto updateAnswer = new AnswerDto("답변테스트2");
		basicAuthTemplate(defaultUser()).put(location, updateAnswer);
		
		AnswerDto saveAnswer = getResource(location, AnswerDto.class);
		assertThat(saveAnswer.getContents(), is(updateAnswer.getContents()));
	}
	
	@Test
	public void delete_다른_사람() throws Exception{
		long questionId = getResource(createQuestion(), QuestionDto.class, defaultUser()).getId();
		String location = createAnswer(questionId);
		basicAuthTemplate(newUser("ssosso4")).delete(location);
		
		long deleteId = Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
		assertThat(answerRepository.findOne(deleteId).get().isDeleted(), is(false));
	}
	
	@Test
	public void delete() throws Exception{
		long questionId = getResource(createQuestion(), QuestionDto.class, defaultUser()).getId();
		String location = createAnswer(questionId);
		basicAuthTemplate(defaultUser()).delete(location);
		
		long deleteId = Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
		assertThat(answerRepository.findOne(deleteId).get().isDeleted(), is(true));
	}
}
