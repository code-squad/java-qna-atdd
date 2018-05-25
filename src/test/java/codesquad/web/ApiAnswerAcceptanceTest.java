package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import codesquad.domain.AnswerRepository;
import codesquad.domain.QuestionRepository;
import codesquad.dto.QuestionDto;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest {

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private AnswerRepository answerRepository;

	private static final Long EXIST_QUESTION = 1L;
	private static final Long EXIST_ANSWER = 3L;
	
	@Test
	public void create() {
		String location = createResource(basicAuthTemplate(), String.format("/api/answers/%d", EXIST_QUESTION), "내용은5자이상");
		assertThat(getResource(location, QuestionDto.class, defaultUser()), is(questionRepository.findById(EXIST_QUESTION).get().toQuestionDto()));
	}
	@Test
	public void create_noLogin() {
		 ResponseEntity<String> response = template().postForEntity(String.format("/api/answers/%d", EXIST_QUESTION), "내용은5자이상", String.class);
         assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
	}

	@Test
	public void delete_noOwner() {
		assertThat(answerRepository.findById(EXIST_ANSWER).isPresent(), is(true));
		basicAuthTemplate().delete(String.format("/api/answers/%d", EXIST_ANSWER));
		assertThat(answerRepository.findById(EXIST_ANSWER).get().isDeleted(), is(false));
	}
	@Test
	public void delete() {
		assertThat(answerRepository.findById(EXIST_ANSWER).isPresent(), is(true));
		basicAuthTemplate(defaultOtherUser()).delete(String.format("/api/answers/%d", EXIST_ANSWER));
		assertThat(answerRepository.findById(EXIST_ANSWER).get().isDeleted(), is(true));
	}

}
