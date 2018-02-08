package codesquad.service;

import codesquad.*;
import codesquad.domain.*;
import codesquad.dto.QuestionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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

    public Question create(User loginUser, QuestionDto questionDto) {
        if (loginUser.isGuestUser()) {
            throw new UnAuthorizedException();
        }
        Question question = questionDto.toQuestion();
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findById(long id) {
        return questionRepository.findOne(id);
    }

    @Transactional
    public void update(User loginUser, long id, QuestionDto questionDto) throws UnAuthenticationException {
        Question question = questionRepository.findOne(id);

        if (ObjectUtils.isEmpty(question)) {
            throw new NullPointerException();
        }

        question.update(loginUser, questionDto.toQuestion());
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question question = questionRepository.findOne(questionId);

        if (ObjectUtils.isEmpty(question)) {
            throw new CannotDeleteException("해당 질문이 존재하지 않습니다.");
        }

        question.delete(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) throws CannotAddException {
        Question question = this.questionRepository.findOne(questionId);
        if (ObjectUtils.isEmpty(question)) {
            throw new CannotAddException("답변을 달 수 없습니다.");
        }

        Answer answer = answerRepository.save(new Answer(0L, loginUser, question, contents));
        return question.addAnswer(answer);
    }

    @Transactional
    public void updateAnswer(User user, Long answerID, String contents) throws CannotUpdateException {
        Answer answer = this.findAnswerById(answerID);
        if (ObjectUtils.isEmpty(answer)) {
            throw new CannotUpdateException("수정 할 수 없습니다.");
        }
        answer.upadte(user, contents);
    }

    public void deleteAnswer(User loginUser, long answerID) throws CannotDeleteException {
        Answer answer = this.findAnswerById(answerID);
        if (ObjectUtils.isEmpty(answer)) {
            throw new CannotDeleteException("삭제 할 수 없습니다.");
        }
        answer.delete(loginUser);
    }

    @Transactional
    public List<Answer> findAnswersByQuestionId(Long id) {
        return this.findById(id).getAnswers();
    }

    public Answer findAnswerById(Long id) {
        return answerRepository.findOne(id);
    }
}
