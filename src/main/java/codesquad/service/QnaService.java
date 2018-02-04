package codesquad.service;

import codesquad.AnswerNotFoundException;
import codesquad.QuestionNotFoundException;
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.DeleteHistory;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
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

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findQuestionById(long id) {
        return Optional.ofNullable(questionRepository.findOne(id));
    }

    public Question findQuestionByIdAndNotDeleted(long id) {
        Question question = questionRepository.findOne(id);
        if (question == null || question.isDeleted()) {
            throw new QuestionNotFoundException(id);
        }
        return question;
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question question = findQuestionByIdAndNotDeleted(id);

        return question.update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long id) {
        Question question = findQuestionByIdAndNotDeleted(id);

        List<DeleteHistory> histories = question.delete(loginUser);
        deleteHistoryService.saveAll(histories);
    }

    @Transactional
    public Answer addAnswer(Answer answer, long questonId) {
        Question question = findQuestionByIdAndNotDeleted(questonId);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public Answer updateAnswer(long id, Answer updateAnswer) {
        Answer answer = findAnswerByIdAndNotDeleted(id);
        return answer.update(updateAnswer);
    }

    @Transactional
    public void deleteAnswer(User loginUser, long id) {
        Answer answer = findAnswerByIdAndNotDeleted(id);
        answer.delete(loginUser);
    }

    public Answer findAnswerByIdAndNotDeleted(long id) {
        Answer answer = answerRepository.findOne(id);
        if (answer == null || answer.isDeleted()) {
            throw new AnswerNotFoundException(id);
        }
        return answer;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }
}
