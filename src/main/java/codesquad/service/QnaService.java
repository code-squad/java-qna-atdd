package codesquad.service;

import codesquad.domain.*;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
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

    @Transactional
    public Question create(User loginUser, Question question) {
        question.writeBy(loginUser);
        return questionRepository.save(question);
    }

    public Question findQuestionById(long id) {
        Question question = questionRepository.findOne(id);
        if (question == null) {
            throw new EntityNotFoundException(id + " Question not found");
        }
        return question;
    }

    @Transactional
    public Question update(User loginUser, long questionId, QuestionDto questionDto) {
        Question selectedQuestion = findQuestionById(questionId);
        return selectedQuestion.update(loginUser, questionDto);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        Question question = findQuestionById(questionId);
        question.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, Answer answer) {
        Question question = findQuestionById(questionId);
        answer.writedBy(loginUser);
        question.addAnswer(answer);
        return answer;
    }

    public Answer findAnswerById(long answerId) {
        Answer answer = answerRepository.findOne(answerId);
        if (answer == null) {
            throw new EntityNotFoundException(answerId + " Answer not found");
        }
        return answer;
    }

    @Transactional
    public void deleteAnswer(User loginUser, long answerId) {
        Answer answer = findAnswerById(answerId);
        answer.delete(loginUser);
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long answerId, AnswerDto answerDto) {
        Answer selectedAnswer = findAnswerById(answerId);
        return selectedAnswer.update(loginUser, answerDto);
    }
}
