package codesquad.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.CannotDeleteException;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question add(User loginUser, QuestionDto questionDto) {
        return create(loginUser, questionDto.toQuestion());
    }

    public Answer addAnswer(User loginUser, long questionId, Answer answer) {
        answer.writeBy(loginUser);
        Question question = findById(questionId);
        question.addAnswer(answer);
        log.debug("answer : {}", answer);
        log.debug("question : {}", findById(questionId));

        return answerRepository.save(answer);
    }


    public Question findById(long id) {
        return questionRepository.findOne(id);
    }

    public Answer findAnswerById(long id) {
        return answerRepository.findOne(id);
    }

    @Transactional
    public void updateQuestion(User loginUser, long id, Question updatedQuestion) throws CannotDeleteException {
        Question original = findById(id);
        original.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question target = questionRepository.findOne(questionId);
        List<DeleteHistory> histories = new ArrayList<>();
        histories.addAll(target.delete(loginUser));
        deleteHistoryService.saveAll(histories);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) {
        Answer target = answerRepository.findOne(id);
        target.delete(loginUser);
    }

    @Transactional
    public void deleteAnswers(User loginUser, List<Answer> answers) {
        answers.forEach(answer -> deleteAnswer(loginUser, answer.getId()));
    }
}
