package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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

    public Question findById(User loginUser, long id) {
        return questionRepository.findById(id)
                .filter(x -> x.isOwner(loginUser))
                .orElseThrow(UnAuthorizedException::new); //TODO
    }

    public Question findById(long id) {
        return questionRepository.findById(id)
                .orElseThrow(UnAuthorizedException::new);
    }

    public Answer findByAnswerId(long id) {
        return answerRepository.findById(id)
                .orElseThrow(UnAuthorizedException::new);
    }

    @Transactional
    public Question updateQuestion(User loginUser, long id, Question updatedQuestion) {
        Question original = questionRepository.findById(id)
                .orElseThrow(UnAuthorizedException::new);

        original.update(loginUser, updatedQuestion);
        return original;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(UnAuthorizedException::new);

        List<DeleteHistory> histories = question.delete(loginUser);
        deleteHistoryService.saveAll(histories);
        return question;
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, Answer updateAnswer) {
        Answer original = answerRepository.findById(id)
                .orElseThrow(UnAuthorizedException::new);

        original.update(loginUser, updateAnswer);
        return original;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        // TODO 답변 추가 기능 구현
        log.debug("contents : {}", contents);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(UnAuthorizedException::new);
        log.debug("find question : {}", question);
        Answer answer = new Answer(loginUser, contents);
        log.debug("find answer : {}", answer);


        return answerRepository.save(answer);
    }

    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현
        Answer answer = answerRepository.findById(id)
                .orElseThrow(UnAuthorizedException::new);
        answer.delete(loginUser);
        return answerRepository.save(answer);
    }
}
