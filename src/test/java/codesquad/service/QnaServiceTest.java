package codesquad.service;

import codesquad.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
	@Mock
	private QuestionRepository questionRepository;
	
	@Mock
	private AnswerRepository answerRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private QnaService qnaService;
	
	private static final User SSOSSO = new User(0L,"ssosso", "password", "name", "ssossohow@gmail.com");
	
	private static Question newDefaultQuestion(User writer) {
		Question question = new Question("제목", "내용");
		question.writeBy(writer);
		return question;
	}

	@Test
	public void create_success() {
		Question question = newDefaultQuestion(SSOSSO);
		when(questionRepository.save(question)).thenReturn(question);
		
		Question createQuestion = qnaService.createQuestion(SSOSSO, question);
		assertThat(createQuestion, is(question));
		assertThat(createQuestion.isOwner(SSOSSO), is(true));
	}
}
