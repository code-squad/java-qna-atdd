package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.QuestionDto;
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

    public Question create(User loginUser, QuestionDto newQuestion) {
        Question question = newQuestion.toQuestion();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, long id, QuestionDto updateQuestionDto) throws UnAuthorizedException {
        Optional<Question> question = questionRepository.findById(id);
        if (!question.isPresent()) {
            throw new NullPointerException("Question Update Error");
        }
        return question.get().update(loginUser, updateQuestionDto.toQuestion());
    }

    @Transactional
    public DeleteHistories delete(User loginUser, long questionId) throws CannotDeleteException {
        Optional<Question> question = questionRepository.findById(questionId);
        if (!question.isPresent()) {
            throw new CannotDeleteException("Requested question is not exist.");
        }

        DeleteHistories histories = question.get().delete(loginUser);
        deleteHistoryService.saveAll(histories.toList());
        return histories;
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (!question.isPresent()) {
            throw new NullPointerException("Not exist question.");
        }
        Answer answer = new Answer(loginUser, contents);
        answerRepository.save(answer);
        question.get().addAnswer(answer);
        return answer;
    }

    public Answer deleteAnswer(User loginUser, long id) {
        Optional<Answer> answer = answerRepository.findById(id);
        if (!answer.isPresent()) {
            throw new NullPointerException("Not exist answer.");
        }
        deleteHistoryService.save(answer.get().delete());
        return answer.get();
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, Answer updateAnswer) {
        Optional<Answer> answer = answerRepository.findById(id);
        if (!answer.isPresent()) {
            throw new NullPointerException("Not exist answer.");
        }
        return answer.get().update(loginUser, updateAnswer);
    }

    public Answer getAnswer(long answerId) {
        Answer answer = answerRepository.findById(answerId).get();
        log.debug("answer : {}", answer);
        return answer;
    }
}
