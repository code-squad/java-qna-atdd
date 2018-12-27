package codesquad.service;

import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service("qnaService")  //서비스에서는 비즈니스 로직을 구현하는 곳이 아니다!! , thin layer - 가볍게 구현해야 한다.
                        //상태값을 가지는 곳은 domain이다.
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question create(User loginUser, Question question) {
        question.writtenBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findByQuestionId(long id) {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        //존재 하지 않는 질문을 수정하고자 할때 bad request란 에러를 발생시킴
    }

    public Answer findByAnswerId(long id) {
        return answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        return findByQuestionId(id).modify(updatedQuestion, loginUser);
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) {
        return findByQuestionId(questionId).delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addReply(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        Answer newAnswer = new Answer(loginUser, contents);
        return findByQuestionId(questionId).addAnswer(loginUser, newAnswer);
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long answerId) {
        // TODO 답변 삭제 기능 구현
        return findByAnswerId(answerId).delete(loginUser);
    }
}
