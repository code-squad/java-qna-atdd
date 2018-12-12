package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.CannotFindException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
        return questionRepository.findById(id).orElseThrow(CannotFindException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question question = findById(id);
        return question.update(loginUser, updatedQuestion);
//        return questionRepository.save(question);
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId);
        question.delete(loginUser);
        return questionRepository.save(question);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = questionRepository.findById(questionId).orElseThrow(CannotFindException::new);
        Answer answer = new Answer(loginUser, question, contents);
//        Answer answer = new An
        return answerRepository.save(answer);
    }

    public Iterable<Answer> findAllAnswers(long id) {
        return answerRepository.findByDeleted(false);
    }

    public Answer findByAnswerId(long id) {
        return answerRepository.findById(id).orElseThrow(CannotFindException::new);
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현 
        return null;
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long answerId, String contents) {
        Answer answer = answerRepository.findById(answerId).orElseThrow(CannotFindException::new);
        return answer.update(loginUser, contents);
    }
}
