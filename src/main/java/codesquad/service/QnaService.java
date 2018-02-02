package codesquad.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findOneByIdAndDeleted(id, false);
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question question = findById(id);
        question.updateBy(updatedQuestion, loginUser);
        return question;
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = findById(questionId);

        if (question == null) {
            throw new CannotDeleteException("questions is not exist");
        }

        question.deleteBy(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    // [NEED TO STUDY] @Transactional's specific role
    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Question question = findById(questionId);

        if (question == null) {
            throw new IllegalStateException("question is not exist. questionId=" + questionId);
        }

        Answer answer = new Answer(loginUser, contents);
        question.addAnswer(answer);

        return answer;
    }

    @Transactional
    public void updateAnswer(User loginUser, long questionId, long answerId, String contents) {
        Answer answer = answerRepository.findOne(answerId);
        answer.updatedBy(loginUser, questionId, contents);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long questionId, long answerId) throws CannotDeleteException {
        Answer answer = answerRepository.findOne(answerId);

        if (answer == null) {
            throw new CannotDeleteException("answer is not exist. answerId=" + answerId);
        }

        answer.deleteBy(loginUser, questionId);
    }
}
