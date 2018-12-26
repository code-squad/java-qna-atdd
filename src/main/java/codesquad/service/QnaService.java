package codesquad.service;

import codesquad.exception.CannotDeleteException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
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

    public Question add(Question question) {
        return questionRepository.save(question);
    }

    @Transactional
    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        add(question);
        return question;
    }

    public Question findById(long id) {
        return questionRepository.findById(id)
                .orElseThrow(NoResultException::new);
    }

    public Answer findByAnswerId(long id) {
        return answerRepository.findById(id)
                .orElseThrow(NoResultException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question original = findById(id);
        original.update(loginUser, updatedQuestion);
        return original;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long id) throws CannotDeleteException {
        Question target = findById(id);
        deleteHistoryService.saveAll(target.delete(loginUser));
        return target;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long id, String contents) {
        Question target = findById(id);
        return target.addAnswer(new Answer(loginUser, contents));
    }

    @Transactional
    public Question deleteAnswer(User loginUser, long id, long answerId) throws CannotDeleteException {
        Question question = findById(id);
        Answer target = findByAnswerId(answerId);

        deleteHistoryService.saveAll(target.delete(loginUser));
        return question;
    }
}