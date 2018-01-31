package codesquad.service;

import codesquad.CannotManageException;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import support.test.AcceptanceTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class QnaServiceTest extends AcceptanceTest {
    private Question question;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QnaService qnaService;

    @Before
    public void init() {
        answerRepository.deleteAll();
        questionRepository.deleteAll();
        makeQuestion("TestTitle", "테스트당");
    }

    private Question makeQuestion(String title, String contents) {
        User javajigi = findByUserId("javajigi");
        question = new Question(title, contents);
        question.writeBy(javajigi);
        return question;
    }

    @Test
    public void 질문_저장하기_테스트() {
        Question saveQuestion = questionRepository.save(question);
        assertEquals(question.getTitle(), saveQuestion.getTitle());
        assertEquals(question.getContents(), saveQuestion.getContents());
    }

    @Test
    public void 질문_리스트_불러오기_테스트() {
        List<Question> questions = IntStream.range(0, 3)
                .mapToObj(i -> makeQuestion("TestTitle"+i, "테스트당"+i))
                .collect(Collectors.toList());

        questionRepository.save(questions);
        List<Question> savedQuestions = qnaService.findAll(new PageRequest(0, 10)).getContent();
        assertEquals(3, savedQuestions.size());
    }

    @Test
    public void 질문_수정하기_테스트() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        User javajigi = findByUserId("javajigi");
        saveQuestion.update(javajigi, new Question("updateTitle", "updateContents"));
        Question updatedQuestion = qnaService.update(javajigi, saveQuestion.getId(), saveQuestion);

        assertEquals(saveQuestion.getTitle(), updatedQuestion.getTitle());
        assertEquals(saveQuestion.getContents(), updatedQuestion.getContents());
    }

    @Test
    public void 질문_삭제하기_테스트() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        assertFalse(saveQuestion.isDeleted());
        saveQuestion.deleted(findByUserId("javajigi"));
        assertTrue(saveQuestion.isDeleted());
    }

    @Test(expected = CannotManageException.class)
    public void 자신의_질문에만_수정이_가능한가() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        qnaService.update(findByUserId("sanjigi"), saveQuestion.getId(), saveQuestion);
    }

    @Test(expected = CannotManageException.class)
    public void 자신의_질문에만_삭제가_가능한가() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        qnaService.deleteQuestion(findByUserId("sanjigi"), saveQuestion.getId());
    }

    @Test(expected = CannotManageException.class)
    public void 삭제된_글을_수정하려_했는가() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        User javajigi = findByUserId("javajigi");

        qnaService.deleteQuestion(javajigi, saveQuestion.getId());
        questionRepository.findOne(saveQuestion.getId()).update(javajigi, new Question("updateTitle", "updateContents"));
    }

    @Test(expected = CannotManageException.class)
    public void 삭제된_글을_삭제하려_했는가() throws CannotManageException {
        Question saveQuestion = questionRepository.save(question);
        User javajigi = findByUserId("javajigi");
        qnaService.deleteQuestion(javajigi, saveQuestion.getId());
        qnaService.deleteQuestion(javajigi, saveQuestion.getId());
    }
}
