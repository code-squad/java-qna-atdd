package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;
import codesquad.dto.QuestionDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
	private static final Logger logger = LoggerFactory.getLogger(QnaServiceTest.class);

	@Mock
	private UserRepository userRepository;

	@Mock
	private QuestionRepository questionRepository;

	@InjectMocks
	private UserService userService;

	@InjectMocks
	private QnaService qnaService;

	@Test
	public void create() throws Exception {
		User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
		when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
		User loginUser = userService.login(user.getUserId(), user.getPassword());

		Question question = new Question("질문제목", "질문본문");
		when(questionRepository.save(question)).thenReturn(question);
		Question insertedQuestion = qnaService.create(loginUser, question);

		assertThat(insertedQuestion, is(question));
	}

	@Test
	public void update() throws Exception {
		User user = new User("sanjigi", "password", "name", "javajigi@slipp.net");
		when(userRepository.findByUserId(user.getUserId())).thenReturn(Optional.of(user));
		User loginUser = userService.login(user.getUserId(), user.getPassword());

		Question question1 = new Question("질문제목1", "질문본문1");
		when(questionRepository.save(question1)).thenReturn(question1);
		qnaService.create(loginUser, question1);
		logger.debug("data: {}", questionRepository.findAll());

		QuestionDto questionDto = new QuestionDto("질문제목2", "질문본문2");
		logger.debug("question2: {}", questionDto);

		Question updatedQuestion = qnaService.update(loginUser, 1, questionDto);
		logger.debug("updatedQuestion: {}", updatedQuestion);

		assertThat(updatedQuestion, is(questionDto));
	}

}
