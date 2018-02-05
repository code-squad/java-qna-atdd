package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.CannotFindException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.QuestionDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private QnaService qnaService;

    private User writer;

    @Before
    public void setup() {
        writer = new User("testId", "testP", "testN", "test@slipp.net");
    }

    @Test
    public void update() {
        Question question = new Question(1, "title", "contents", writer);
        when(questionRepository.findOne(question.getId())).thenReturn(question);

        QuestionDto updateQuestionDto = new QuestionDto(1, "updateTitle", "updateContents");
        qnaService.update(writer, updateQuestionDto);
        Question updateQuestion = updateQuestionDto.toQuestion();
        updateQuestion.writeBy(writer);
        assertThat(updateQuestion, is(question));
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_wrong_writer() {
        User wrongWriter = new User("testId2", "testP", "testN", "test@slipp.net");
        Question question = new Question(1, "title", "contents", writer);
        when(questionRepository.findOne(question.getId())).thenReturn(question);

        QuestionDto updateQuestion = new QuestionDto(1, "updateTitle", "updateContents");
        qnaService.update(wrongWriter, updateQuestion);
        assertThat(updateQuestion, not(question));
    }

    @Test
    public void delete() throws Exception {
        User writer = new User(2, "sanjigi", "test", "산지기", "sanjigi@slipp.net");
        Question question = new Question(1, "title", "contents", writer);
        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(questionRepository.exists(question.getId())).thenReturn(true);

        qnaService.deleteQuestion(writer, question.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_답글이_있을_때() throws Exception {
        Question question = new Question(1, "title", "contents", writer);
        question.addAnswer(new Answer());
        when(questionRepository.findOne(question.getId())).thenReturn(question);

        qnaService.deleteQuestion(writer, question.getId());
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_글이_없을_때() throws Exception {
        Question question = new Question(10, "title", "contents", writer);
        question.addAnswer(new Answer());
        when(questionRepository.findOne(question.getId())).thenReturn(question);
        when(questionRepository.exists(question.getId())).thenReturn(true);

        qnaService.deleteQuestion(writer, question.getId());
    }

    @Test
    public void 답글_추가() throws Exception {
        Question question = new Question(1, "title", "contents", writer);
        when(questionRepository.findOne(1L)).thenReturn(question);

        assertFalse(question.notEmptyAnswer());
        qnaService.addAnswer(writer, 1L, "contents");
        assertTrue(question.notEmptyAnswer());
    }

    @Test(expected = CannotFindException.class)
    public void 글이_없을_때_답글_추가() throws Exception {
        when(questionRepository.findOne(1L)).thenReturn(null);

        qnaService.addAnswer(writer, 1L, "contents");
    }

    @Test
    public void 답글_검색() throws Exception {
        Question question = new Question(1L, "title", "contents", writer);
        Answer answer = new Answer(1L, writer, question, "contents");
        when(answerRepository.exists(1L)).thenReturn(true);
        when(answerRepository.findOne(1L)).thenReturn(answer);

        Answer returnAnswer = qnaService.findAnswer(1L, 1L);
        assertThat(answer, is(returnAnswer));
    }

    @Test(expected = CannotFindException.class)
    public void 없는_답글_검색() throws Exception {
        when(answerRepository.exists(1L)).thenReturn(false);

        qnaService.findAnswer(1L, 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void 글에_없는_답글_검색() throws Exception {
        Question question = new Question(1L, "title", "contents", writer);
        Answer answer = new Answer(1L, writer, question, "contents");
        when(answerRepository.exists(1L)).thenReturn(true);
        when(answerRepository.findOne(1L)).thenReturn(answer);

        qnaService.findAnswer(3L, 1L);
    }

    @Test
    public void 답글_수정() throws Exception {
        Question question = new Question(1L, "title", "contents", writer);
        Answer answer = new Answer(1L, writer, question, "contents");
        when(answerRepository.findOne(1L)).thenReturn(answer);
        when(answerRepository.exists(1L)).thenReturn(true);

        Answer updateAnswer = qnaService.updateAnswer(writer, 1L, 1L, "updateContents");
        assertThat(updateAnswer, is(answer));
    }

    @Test(expected = CannotFindException.class)
    public void 없는_답글_수정() throws Exception {
        when(answerRepository.exists(1L)).thenReturn(false);

        qnaService.updateAnswer(writer, 1L, 1L, "updateContents");
    }

    @Test(expected = UnAuthorizedException.class)
    public void 작성자가_아닐_때_답글_수정() throws Exception {
        Question question = new Question(1L, "title", "contents", writer);
        Answer answer = new Answer(1L, writer, question, "contents");
        User wrongWriter = new User("testId2", "testP", "testN", "test@slipp.net");
        when(answerRepository.findOne(1L)).thenReturn(answer);
        when(answerRepository.exists(1L)).thenReturn(true);

        qnaService.updateAnswer(wrongWriter, 1L, 1L, "updateContents");
    }

    @Test(expected = IllegalArgumentException.class)
    public void 잘못된_글에서_답글_수정() throws Exception {
        Question question = new Question(1L, "title", "contents", writer);
        Answer answer = new Answer(1L, writer, question, "contents");
        when(answerRepository.findOne(answer.getId())).thenReturn(answer);
        when(answerRepository.exists(answer.getId())).thenReturn(true);

        qnaService.updateAnswer(writer, 3L, 1L, "updateContents");
    }

    @Test
    public void 답글_삭제() throws Exception {
        Question question = new Question(1L, "title", "contents", writer);
        Answer answer = new Answer(1L, writer, question, "contents");
        when(answerRepository.findOne(answer.getId())).thenReturn(answer);
        when(answerRepository.exists(answer.getId())).thenReturn(true);

        qnaService.deleteAnswer(writer, 1L);
        assertTrue(answer.isDeleted());
    }

    @Test(expected = CannotFindException.class)
    public void 없는_답글_삭제() throws Exception {
        Question question = new Question(1L, "title", "contents", writer);
        Answer answer = new Answer(1L, writer, question, "contents");
        when(answerRepository.findOne(answer.getId())).thenReturn(answer);
        when(answerRepository.exists(answer.getId())).thenReturn(false);

        qnaService.deleteAnswer(writer, 1L);
    }

    @Test(expected = UnAuthorizedException.class)
    public void 작성자가_아닐_때_답글_삭제() throws Exception {
        User wrongWriter = new User("testId2", "testP", "testN", "test@slipp.net");
        Question question = new Question(1L, "title", "contents", writer);
        Answer answer = new Answer(1L, wrongWriter, question, "contents");
        when(answerRepository.findOne(answer.getId())).thenReturn(answer);
        when(answerRepository.exists(answer.getId())).thenReturn(true);

        qnaService.deleteAnswer(writer, 1L);
    }
}
