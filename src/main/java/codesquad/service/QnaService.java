package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.AnswerDto;
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
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findOne(id);
    }

    public Answer findByIdAnswer(long id) {
        return answerRepository.findOne(id);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = questionRepository.findOne(id);
        original.update(loginUser, updatedQuestion);
        return original;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findOne(questionId);
        question.delete(loginUser);
        return question;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, Answer answer) {
        answer.writeBy(loginUser);
        Question question = questionRepository.findOne(questionId);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        Answer answer = answerRepository.findOne(id);
        answer.delete(loginUser);
    }
}
