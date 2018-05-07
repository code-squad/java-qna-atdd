package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import codesquad.UnAuthenticationException;
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
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findOne(id);
    }

    @Transactional
    public void update(User loginUser, long id, Question updatedQuestion) {
        // TODO 수정 기능 구현
        Question original = questionRepository.findOne(id);
        original.update(loginUser,updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws UnAuthenticationException {
        // TODO 삭제 기능 구현 ATDD 4단계
        Question original = Optional.of(questionRepository.findOne(questionId)).orElseThrow(NullPointerException::new);
        List<DeleteHistory> histories = original.deleteQuestion(loginUser);
        deleteHistoryService.saveAll(histories);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public void addAnswer(User loginUser, long questionId, Answer answer) throws UnAuthenticationException {
        Question original = findById(questionId);
        original.addAnswer(answer);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long answerId) throws UnAuthenticationException {
        // TODO 답변 삭제 기능 구현
        Answer answer = answerRepository.findOne(answerId);
        answer.delete(loginUser);
    }

    @Transactional
    public void updateAnswer(User loginUser, long answerId, Answer updatedAnswer) {
        // TODO 수정 기능 구현
        Answer original = answerRepository.findOne(answerId);
        original.update(loginUser,updatedAnswer);
    }
}
