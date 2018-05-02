package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;

import codesquad.CannotUpdateException;
import codesquad.UnAuthorizedException;
import codesquad.dto.QuestionDto;
import org.hibernate.ObjectNotFoundException;
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
    
    @Transactional
    public Question createQuestion(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Question findQuestionById(long id) {
        return questionRepository.findOne(id)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 질문이므로 조회할 수 없습니다."));
    }

    @Transactional
    public void updateQuestion(User loginUser, long id, Question updatedQuestion) throws CannotUpdateException {
        findQuestionById(id).update(loginUser, updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(User loginUser, long id) throws CannotDeleteException {
        findQuestionById(id).delete(loginUser);
    }

    public Iterable<Question> findAllQuestionNotDeleted() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findListQuestionNotDeleted(Pageable pageable) {
        return questionRepository.findByDeleted(false, pageable);
    }

    public List<Question> findListQuestion(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }
    
    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer answer = new Answer(loginUser, contents);
        findQuestionById(questionId).addAnswer(answer);
        return answer;
    }
    
    public Answer findAnswerById(long id) {
        return answerRepository.findOne(id)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 답변이므로 조회할 수 없습니다."));
    }
    
    @Transactional
    public void deleteAnswer(User loginUser, long id) throws CannotDeleteException {
        findAnswerById(id).delete(loginUser);
    }
    
    @Transactional
    public void updateAnswer(User loginUser, long id, Answer updateAnswer) throws CannotUpdateException {
        findAnswerById(id).update(loginUser, updateAnswer);
    }
}
