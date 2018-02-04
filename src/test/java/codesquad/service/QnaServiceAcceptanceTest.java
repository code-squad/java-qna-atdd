package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.QuestionNotFoundException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.DeleteHistoryRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class QnaServiceAcceptanceTest {

    @Autowired
    private QnaService qnaService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    private User javajigi;

    private User gunju;

    @Before
    public void setUp() throws Exception {
        javajigi = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");
        gunju = new User(2, "gunju", "test", "건주", "test@email.com");
    }

    @Test
    public void create() throws Exception {
        Question question = qnaService.create(javajigi, new Question("test", "test"));

        assertThat(question).isNotNull();
        assertThat(questionRepository.findOne(question.getId())).isNotNull();
    }

    @Test
    public void findById() throws Exception {
        Optional<Question> question = qnaService.findQuestionById(1L);
        assertThat(question.isPresent()).isTrue();
    }

    @Test
    public void findById_ID에해당하는데이터가없는경우() throws Exception {
        Optional<Question> question = qnaService.findQuestionById(100L);
        assertThat(question.isPresent()).isFalse();
    }

    @Test
    public void findByIdAndNotDeleted_삭제된Question() throws Exception {
        Optional<Question> question = qnaService.findQuestionById(3L);
        assertThat(question.isPresent()).isFalse();
    }

    @Test
    public void update() throws Exception {
        Question question = qnaService.update(javajigi, 1, new Question("update", "update content"));
        assertThat(question).isNotNull();

        Question dbQuestion = questionRepository.findOne(question.getId());
        assertThat(dbQuestion).isNotNull();
        assertThat(dbQuestion.getTitle()).isEqualTo("update");
        assertThat(dbQuestion.getContents()).isEqualTo("update content");

    }

    @Test(expected = UnAuthorizedException.class)
    public void update_권한이없는경우() throws Exception {
        User user = new User(2, "sanjigi", "test", "산지기", "sanjigi@slipp.net");
        qnaService.update(user, 1, new Question("update", "update content"));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_파라미터가NULL인경우() throws Exception {
        qnaService.update(null, 1, new Question("update", "update content"));

    }

    @Test(expected = QuestionNotFoundException.class)
    public void update_해당Question이존재하지않는경우() throws Exception {
        qnaService.update(javajigi, 10, new Question("update", "update content"));
    }

    @Test(expected = QuestionNotFoundException.class)
    public void update_해당Question삭제된경우() throws Exception {
        qnaService.update(javajigi, 3, new Question("update", "update content"));
    }

    @Test
    public void deleteQuestion() throws Exception {
        qnaService.deleteQuestion(javajigi, 4);
        assertThat(questionRepository.findOne(4L).isDeleted()).isTrue();
        assertThat(deleteHistoryRepository.count()).isEqualTo(1);
    }

    @Test(expected = CannotDeleteException.class)
    public void deleteQuestion_질문자가아닌다른사람의답변이존재하는경우() throws Exception {
        qnaService.deleteQuestion(javajigi, 1);
        assertThat(questionRepository.findOne(1L).isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteQuestion_권한이없는경우() throws Exception {
        User user = new User(2, "sanjigi", "test", "산지기", "sanjigi@slipp.net");
        qnaService.deleteQuestion(user, 1);
    }

    @Test(expected = QuestionNotFoundException.class)
    public void deleteQuestion_이미존재하지않는경우() throws Exception {
        qnaService.deleteQuestion(javajigi, 10);
    }

    @Test
    public void addAnswer() throws Exception {
        Question question = questionRepository.findOne(1L);
        Answer answer = new Answer(javajigi, "test contents");
        qnaService.addAnswer(answer, 1L);

        assertThat(question.getCountOfAnswers()).isEqualTo(3);
    }

    @Test
    public void updateAnswer() throws Exception {
        Answer updateAnswer = new Answer(javajigi, "내용 업데이트");

        qnaService.updateAnswer(1L, updateAnswer);
        Answer dbAnswer = answerRepository.findOne(1L);
        assertThat(dbAnswer.getContents()).isEqualTo("내용 업데이트");
    }

    @Test
    public void deleteAnswer() throws Exception {
        qnaService.deleteAnswer(javajigi, 1);
        Answer answer = answerRepository.findOne(1L);
        assertThat(answer.isDeleted()).isTrue();
    }

    @Test(expected = UnAuthorizedException.class)
    public void deleteAnswer_권한이없는사용자() throws Exception {
        qnaService.deleteAnswer(gunju, 1);
    }

}