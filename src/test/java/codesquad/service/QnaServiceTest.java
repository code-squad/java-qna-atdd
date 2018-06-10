package codesquad.service;

import codesquad.UnAuthorizedException;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    private static final Logger log = LoggerFactory.getLogger(QnaServiceTest.class);

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QnaService qnaService;

    private Question testQuestion = new Question("test title", "test contents");
    private User testUser = new User("testid", "testpassword", "testname", "test@email.com");

    @Test
    public void questionCreateSuccess() {
        when(questionRepository.save(testQuestion)).thenReturn(testQuestion);
        Question question = qnaService.create(testUser, testQuestion);
        log.debug("question : {}", question);

        assertThat(question.generateUrl(), is("/qna/" + question.getId()));
    }

    @Test(expected = UnAuthorizedException.class)
    public void questionCreateFail() {
        qnaService.create(User.GUEST_USER, testQuestion);
    }
}
