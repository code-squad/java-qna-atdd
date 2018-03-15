package codesquad.web;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.dto.QuestionDto;
import codesquad.service.QnaService;
import support.test.AcceptanceTest;

public class ApiAnswerAcceptanceTest extends AcceptanceTest{
	private static final Logger log = LoggerFactory.getLogger(ApiAnswerAcceptanceTest.class);
	
	@Test
	public void create() throws Exception {
		Answer newAnswer = new Answer(defaultUser(), "답변입니당");
		String location = createResource(basicAuthTemplate(), "/api/questions/1/answers", newAnswer);
		Answer dbAnswer = getResource(location, Answer.class, defaultUser());
		assertThat(dbAnswer.getContents(), is(newAnswer.getContents()));
	}
}
