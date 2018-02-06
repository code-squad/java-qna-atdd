package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;

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
    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findOne(id);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question question = questionRepository.findOne(id);
        return question.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findOne(questionId);
        question.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);
        if (question == null) {
            throw new IllegalArgumentException(questionId + "가 존재하지 않습니다.");
        }
        return question.addAnswer(new Answer(loginUser,contents));
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) {
        Answer answer = answerRepository.findOne(id);
        answer.delete(loginUser);
    }

    @Transactional
    public void updateAnswer(Question question, User loginUser, long answerId, AnswerDto update) {
        Answer answer = answerRepository.findOne(answerId);
        answer.update(loginUser, update);
    }
}
