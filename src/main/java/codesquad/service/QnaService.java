package codesquad.service;

import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import codesquad.NoSuchEntityException;
import codesquad.UnAuthenticationException;
import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    @Transactional
    public Question create(User loginUser, QuestionDto questionDto) {
        Question question = questionDto.toQuestion();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    @Transactional
    public void update(User loginUser, long id, QuestionDto updatedQuestionDto) {
        Question updated = updatedQuestionDto.toQuestion();
        Question original = findQuestionById(id, updated);
        original.updateQuestion(updated, loginUser);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        Question question = findQuestionById(questionId);
        DeleteHistory deletedQuestion = question.deleteQuestion(loginUser);
        List<DeleteHistory> deleted = question.deleteAnswers(loginUser);
        deleted.add(deletedQuestion);
        deleteHistoryService.saveAll(deleted);
    }

    public Question findQuestionById(long id, Question updated) {
        return questionRepository.findById(id)
                .filter(question -> question.equals(updated))
                .orElseThrow(UnAuthenticationException::new);
    }

    public Question findQuestionById(long id) {
        return questionRepository.findById(id)
                .filter(question -> !question.isDeleted())
                .orElseThrow(NoSuchEntityException::new);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer findAnswerById(long id) {
        return answerRepository.findById(id)
                .filter(answer -> !answer.isDeleted())
                .orElseThrow(NoSuchEntityException::new);
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, AnswerDto answerDto) {
        Answer newAnswer = answerDto.toAnswer();
        newAnswer.writeBy(loginUser);
        Question question = findQuestionById(questionId);
        question.addAnswer(newAnswer);
        return answerRepository.save(newAnswer);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) {
        Answer answer = findAnswerById(id);
        DeleteHistory deleteHistory = answer.deleteAnswerByOwner(loginUser);
        deleteHistoryService.saveAll(Collections.singletonList(deleteHistory));
    }
}
