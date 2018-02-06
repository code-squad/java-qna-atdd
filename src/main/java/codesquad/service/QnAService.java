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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service("qnaService")
public class QnAService {
    private static final Logger log = LoggerFactory.getLogger(QnAService.class);
    private static final String DIFFERENT_OWNER = "자신이 작성한 %s에 대해서만 수정/삭제가 가능합니다.";
    private static final String ALREADY_DELETED = "이미 삭제된 %s입니다.";

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    public Question create(User loginUser, QuestionDto questionDto) {
        return questionRepository.save(new Question(loginUser, questionDto));
    }

    public Answer create(User loginUser, Question question, String contents) {
        return answerRepository.save(new Answer(loginUser, question, contents));
    }

    public Question findById(long id) {
        return Optional.ofNullable(questionRepository.findOne(id)).orElseThrow(()->new IllegalArgumentException("존재하지 않는 질문입니다."));
    }

    public Answer findByAnswerId(long id) {
        return Optional.ofNullable(answerRepository.findOne(id)).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다."));
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Question update(User loginUser, long id, Question updatedQuestion) {
        return findById(id).update(loginUser, updatedQuestion);
    }

    @Transactional
    public Answer update(User loginUser, long id, Answer updatedAnswer) {
        return answerRepository.save(findByAnswerId(id).update(loginUser, updatedAnswer));
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) throws CannotDeleteException {
        Question original = findById(questionId);
        if (isDeleted(original)) throw new CannotDeleteException(format(ALREADY_DELETED, "질문"));

        original.delete(loginUser);
        Question updatedQuestion = update(loginUser, questionId, original);
        deleteHistoryService.save(new DeleteHistory(ContentType.QUESTION, updatedQuestion.getId(), loginUser, LocalDateTime.now()));
    }

    @Transactional
    public Answer addAnswer(User loginUser, long questionId, String contents) throws CannotDeleteException {
        Question question = findById(questionId);
        if (isDeleted(question)) throw new CannotDeleteException(format(ALREADY_DELETED, "질문"));
        Answer answer = create(loginUser, question, contents);
        question.addAnswer(answer);
        return answer;
    }

    private boolean isDeleted(Question question) {
        return Optional.ofNullable(deleteHistoryService.findByContentIdAndContentType(question.getId(), ContentType.QUESTION)).isPresent() || question.isDeleted();
    }

    @Transactional
    public void deleteAnswer(User loginUser, long answerId) throws CannotDeleteException {
        Answer answer = findByAnswerId(answerId);
        if (isDeleted(answer)) throw new CannotDeleteException(format(ALREADY_DELETED, "답변"));
        if (!answer.isOwner(loginUser)) throw new UnAuthorizedException(format(DIFFERENT_OWNER,"답변"));

        answer.delete();
        Answer updatedAnswer = update(loginUser, answer.getId(), answer);
        deleteHistoryService.save(new DeleteHistory(ContentType.ANSWER, updatedAnswer.getId(), loginUser, LocalDateTime.now()));
    }

    private boolean isDeleted(Answer answer) {
        return Optional.ofNullable(deleteHistoryService.findByContentIdAndContentType(answer.getId(), ContentType.ANSWER)).isPresent() || answer.isDeleted();
    }
}