package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import codesquad.dto.UserDto;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.ws.Response;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

	private final QuestionDto question = new QuestionDto("질문이다아아아", "질문이다아아아아아");

	@Test
	public void create() throws Exception {
		String location = createResource("/api/questions", question);

		QuestionDto dbQuestion = getResource(location, QuestionDto.class);

		assertThat(dbQuestion, is(question));
		assertThat(dbQuestion.getContents(), is(question.getContents()));
		assertThat(location, is("/api/questions/" + dbQuestion.getId()));
	}

	@Test
	public void showAll() throws Exception {
		createResource("/api/questions", question);
		createResource("/api/questions", question);

		QuestionsDto dbQuestions = getResource("/api/questions", QuestionsDto.class);
		assertThat(dbQuestions.getContents().contains(question), is(true));
	}

	@Test
	public void show() throws Exception {
		String location = createResource("/api/questions", question);

		ResponseEntity<String> response = basicAuthTemplate().getForEntity(location, String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
	}

	@Test
	public void update_owner() throws Exception {
		String location = createResource("/api/questions", question);

		QuestionDto updateQuestion = new QuestionDto("바뀌었다", "바뀐질문이다아");
		basicAuthTemplate().put(location, updateQuestion);

		QuestionDto dbQuestion = getResource(location, QuestionDto.class);

		assertThat(dbQuestion, is(updateQuestion));
	}

	@Test
	public void update_not_owner() throws Exception {
		String location = createResource("/api/questions", question);

		QuestionDto updateQuestion = new QuestionDto("바뀌었다", "바뀐질문이다아");
		User user = new User("hanna", "password", "Hanna", "jks7psj@gmail.com");
		basicAuthTemplate(user).put(location, QuestionDto.class);

		QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
		assertNotEquals(dbQuestion, updateQuestion);
	}

	@Test
	public void delete_owner() throws Exception {
		String location = createResource("/api/questions", question);
		basicAuthTemplate().delete(location);
		QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
		assertNull(dbQuestion);
	}

	@Test
	public void delete_not_owner() throws Exception {
		String location = createResource("/api/questions", question);

		User user = new User("hanna", "password", "Hanna", "jks7psj@gmail.com");
		basicAuthTemplate(user).delete(location);
		QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
		assertNotNull(dbQuestion);
	}
}
