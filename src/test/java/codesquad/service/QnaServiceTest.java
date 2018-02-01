package codesquad.service;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
	@Mock
	private QuestionRepository questionRepository;

	@InjectMocks
	private QnaService qnaService;
	private User user;

	@Before
	public void setup() {
		user = new User("javajigi", "test", "자바지기", "javajigi@slipp.net");
	}

	@Test
	public void 질문_생성() throws Exception {
		Question question = new Question("테스트", "테스트컨텐츠");
		qnaService.create(user, question);

		assertThat(questionRepository.findOne(question.getId()).getTitle(), is("테스트"));
	}
}
