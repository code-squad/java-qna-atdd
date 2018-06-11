package codesquad.service;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import codesquad.dto.QuestionDto;
import codesquad.exceptions.UnAuthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Question findQuestionById(long id) {
        return questionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Question update(User loginUser, long id, QuestionDto updatedQuestion) {
        Question original = findQuestionById(loginUser, id); // 여기서도 유저 매칭 확인,
        return original.update(loginUser, updatedQuestion); // 여기서도 유저 매칭 확인, 같은 확인절차를 반복해서 하는 이유? 더욱 안전하게?
    }

    @Transactional
    public Answer update(User loginUser, long id, String contents) {
        Answer original = findAnswerById(loginUser, id);
        return original.update(loginUser, contents);
    }

    private Answer findAnswerById(User loginUser, long id) {
        return Optional.ofNullable(findAnswerById(id))
                .filter(q -> q.isOwner(loginUser))
                .orElseThrow(() -> new UnAuthorizedException("owner is not matched!"));
    }

    public Question findQuestionById(User loginUser, long id) {
        return Optional.ofNullable(findQuestionById(id))
                .filter(q -> q.isOwner(loginUser))
                .orElseThrow(() -> new UnAuthorizedException("owner is not matched!"));
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) throws UnAuthorizedException{
        Question original = findQuestionById(loginUser, questionId);
        if (original.isDeletable(loginUser)) {
            log.debug("question {} will be deleted", questionId);
            original.logicalDelete();
            deleteHistoryService.registerHistory(loginUser, original);
            return questionRepository.save(original);
        }
        throw new UnAuthorizedException();
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public Answer addAnswer(User loginUser, long questionId, String contents) {
        Answer newAnswer = new Answer(loginUser, contents);
        findQuestionById(questionId).addAnswer(newAnswer);
        return answerRepository.save(newAnswer);
    }

    @Transactional
    public Answer deleteAnswer(User loginUser, long id) {
        Answer original = findAnswerById(loginUser, id);
        return original.logicalDelete();
    }

    public Question addQuestion(@Valid QuestionDto questionDto) {
        return null;
    }

    public Answer findAnswerById(Long id) {
        return answerRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

}
