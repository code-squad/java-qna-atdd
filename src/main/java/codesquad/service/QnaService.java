package codesquad.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.Resource;

import codesquad.UnAuthorizedException;
import codesquad.dto.AnswerDto;
import com.google.common.collect.Lists;
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
        return questionRepository.findOne(id);
    }

    public Question update(User loginUser, long id, Question updatedQuestion) {
        Question originalQuestion = questionRepository.findOne(id);
        originalQuestion.update(updatedQuestion, loginUser);
        return questionRepository.save(originalQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findOne(questionId);
        question.updateDeleteStatus(loginUser);
    }

    public List<Question> findAll() {
        return Lists.newArrayList(questionRepository.findByDeleted(false));
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(long questionId, Answer answer) {
        Question question = questionRepository.findOne(questionId);
        question.addAnswer(answer);
        return answer;
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        // TODO 답변 삭제 기능 구현
        Answer answer = answerRepository.findOne(id);
        answer.updateDelete(loginUser);
        return answer;
    }

    @Transactional
    public Answer updateAnswer(User loginUser, long id, Answer updateContent) {
        Answer answer = answerRepository.findOne(id);
        answer.updateAnswer(loginUser,updateContent);
        return answer;
    }
}
