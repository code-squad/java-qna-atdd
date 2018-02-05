package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
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

    public Question createQuestion(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    @Transactional
    public Answer createAnswer(User loginUser, long targetId, Answer answer) {
        answer.writeBy(loginUser);
        Question target = questionRepository.findOne(targetId);
        target.addAnswer(answer);
        return answer;
    }

    public Question findQuestionById(long id) {
        return questionRepository.findOne(id);
    }

    public Answer findAnswerById(long id) {
        return answerRepository.findOne(id);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question question = questionRepository.findOne(id);
        question.update(loginUser, updatedQuestion);

        return question;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findOne(questionId);
        if (question == null) {
            throw new EntityNotFoundException("해당하는 데이터를 찾지 못하였습니다.");
        }
        deleteHistoryService.save(question.delete(loginUser));
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) {
        Answer answer = answerRepository.findOne(id);
        if (answer == null) {
            throw new EntityNotFoundException("해당하는 데이터를 찾지 못하였습니다.");
        }
        deleteHistoryService.save(answer.delete(loginUser));
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }
}
