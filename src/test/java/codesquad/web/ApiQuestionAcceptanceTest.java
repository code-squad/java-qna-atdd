package codesquad.web;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import codesquad.domain.Answer;
import codesquad.dto.QuestionDto;
import codesquad.service.QnaService;
import support.test.AcceptanceTest;

public class ApiQuestionAcceptanceTest extends AcceptanceTest {

	private static final Logger log = LoggerFactory.getLogger(ApiQuestionAcceptanceTest.class);

	@Test
	public void create() throws Exception {
		QuestionDto newQuestion = new QuestionDto("api create title", "api create contents");
		String location = createResource(basicAuthTemplate(), "/api/questions", newQuestion);
		QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
		assertThat(dbQuestion.getTitle(), is(newQuestion.getTitle()));
		assertThat(dbQuestion.getContents(), is(newQuestion.getContents()));
	}

	@Test
	public void update() throws Exception {
		QuestionDto newQuestion = new QuestionDto("api new for update title", "api new for update contents");
		String location = createResource(basicAuthTemplate(), "/api/questions", newQuestion);
		
		QuestionDto updateQuestion = new QuestionDto(newQuestion.getId(), "api update title", "api update contens");
		basicAuthTemplate(defaultUser()).put(location, updateQuestion);
		
		QuestionDto dbQuestion = getResource(location, QuestionDto.class, defaultUser());
		assertThat(dbQuestion.getTitle(), is(updateQuestion.getTitle()));
		assertThat(dbQuestion.getContents(), is(updateQuestion.getContents()));
	}

}
