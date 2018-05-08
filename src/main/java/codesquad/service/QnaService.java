package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.QuestionDto;
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

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    public Question create(User loginUser, QuestionDto questionDto) {
        Question question = questionDto.toQuestion();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findByIdAndDeleted(id, false);
    }

    public Question findOwnedById(User loginUser, long id) {
        final Question question = findById(id);
        if (!question.isOwner(loginUser)) {
            throw new UnAuthorizedException();
        }
        return question;
    }


    public Question update(User loginUser, long id, Question updatedQuestion) {
        final Question question = questionRepository.findOne(id);
        Question updated = question.update(loginUser, updatedQuestion);

        return questionRepository.save(updated);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        final Question question = questionRepository.findOne(questionId);
        question.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        final Question question = findById(questionId);
        Answer newAnswer = new Answer(loginUser, contents);
        question.addAnswer(newAnswer);
        questionRepository.save(question);
        answerRepository.save(newAnswer);
        return newAnswer;
    }

    public Answer findAnswerById(long questionId, long answerId) {
        final Question question = findById(questionId);
        return question.getAnswer(answerId);
    }

    public Answer deleteAnswer(User loginUser, long questionId, long answerId) {
        Question question = findById(questionId);
        final Answer answer = question.getAnswer(answerId);
        answer.delete(loginUser);
        answerRepository.save(answer);
        return answer;
    }
}
