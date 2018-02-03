package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.QuestionDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {
    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private DeleteHistoryService deleteHistoryService;

    @InjectMocks
    private QnaService qnaService;


    private static final User DEFAULT_WRITER = new User(0L, "javajigi", "password", "name", "javajigi@slipp.net");
    private static final User OTHER_WRITER = new User(1L, "sanjigi", "password", "name", "sanjigi@slipp.net");
    private static final Question DEFAULT_QUESTION = new Question("질문1", "질문있습니다.", DEFAULT_WRITER);

    @Before
    public void setup() {
        given(questionRepository.findOne(DEFAULT_QUESTION.getId())).willReturn(DEFAULT_QUESTION);
    }

    @After
    public void tearDown() {
        try {
            for (Answer answer : DEFAULT_QUESTION.getAnswers()) {
                answer.delete(DEFAULT_WRITER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void create_success() throws Exception {
        QuestionDto create = DEFAULT_QUESTION.toQuestionDto();
        given(questionRepository.save(any(Question.class))).willReturn(create.toQuestion());
        Question created = qnaService.create(DEFAULT_WRITER, DEFAULT_QUESTION);
        assertThat(created.getTitle()).isEqualTo(create.getTitle());
        assertThat(created.getContents()).isEqualTo(create.getContents());
    }

    @Test
    public void update_success() throws Exception{
        QuestionDto update = DEFAULT_QUESTION.toQuestionDto().setTitle("질문2").setContents("질문이 있습니다.");
        given(questionRepository.save(any(Question.class))).willReturn(update.toQuestion());
        Question updated = qnaService.update(DEFAULT_WRITER, DEFAULT_QUESTION.getId(), update);
        assertThat(updated.getTitle()).isEqualTo(update.getTitle());
        assertThat(updated.getContents()).isEqualTo(update.getContents());
    }

    @Test(expected = UnAuthorizedException.class)
    public void update_not_writer() throws Exception{
        QuestionDto update = new QuestionDto("질문2", "질문이 있습니다.");
        qnaService.update(OTHER_WRITER, DEFAULT_QUESTION.getId(), update);
    }

    @Test
    public void delete() throws Exception{
        qnaService.deleteQuestion(DEFAULT_WRITER, DEFAULT_QUESTION.getId());
        given(questionRepository.findOne(DEFAULT_QUESTION.getId())).willReturn(null);
        assertThat(qnaService.findById(DEFAULT_QUESTION.getId())).isNull();
        ArgumentCaptor<List> valueCapture = ArgumentCaptor.forClass(List.class);
        verify(deleteHistoryService, times(1)).saveAll(valueCapture.capture());
        assertThat(valueCapture.getValue().size()).isEqualTo(1);
    }

    @Test
    public void delete_with_answer() throws Exception{
        Answer answer = new Answer(DEFAULT_WRITER, "댓글입니다.1");
        DEFAULT_QUESTION.addAnswer(answer);
        given(answerRepository.findOne(answer.getId())).willReturn(answer);
        given(answerRepository.save(answer)).willReturn(answer);

        qnaService.deleteQuestion(DEFAULT_WRITER, DEFAULT_QUESTION.getId());
        given(questionRepository.findOne(DEFAULT_QUESTION.getId())).willReturn(null);
        assertThat(qnaService.findById(DEFAULT_QUESTION.getId())).isNull();
        ArgumentCaptor<List> valueCapture = ArgumentCaptor.forClass(List.class);
        verify(deleteHistoryService).saveAll(valueCapture.capture());
        assertThat(valueCapture.getValue().size()).isEqualTo(2);
    }

    @Test(expected = CannotDeleteException.class)
    public void delete_not_writer() throws Exception{
        qnaService.deleteQuestion(OTHER_WRITER, DEFAULT_QUESTION.getId());
    }
}
