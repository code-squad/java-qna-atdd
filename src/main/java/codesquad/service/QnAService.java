package codesquad.service;

import codesquad.UnAuthorizedException;
import codesquad.domain.*;
import codesquad.dto.AnswerDto;
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
    private static final String NO_EXISTS = "존재하지 않는 %s입니다.";
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

    public Answer create(User loginUser, AnswerDto answerDto) {
        Question question = findById(answerDto.getQuestionId());
        if (isDeleted(question)) throw new IllegalAccessError(format(ALREADY_DELETED, "질문"));
        return answerRepository.save(new Answer(loginUser, question, answerDto.getContents()));
    }

    public Question findById(long id) {
        return Optional.ofNullable(questionRepository.findOne(id))
                .filter(question -> !isDeleted(question))
                .orElseThrow(()->new IllegalArgumentException(String.format(NO_EXISTS,"질문")));
    }

    public Answer findByAnswerId(long id) {
        return Optional.ofNullable(answerRepository.findOne(id))
                .filter(answer -> !isDeleted(answer))
                .orElseThrow(() -> new IllegalArgumentException(String.format(NO_EXISTS,"답변")));
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Question update(User loginUser, long id, QuestionDto questionDto) {
        return findById(id).update(loginUser, new Question(loginUser, questionDto));
    }

    @Transactional
    public Answer update(User loginUser, long id, AnswerDto answerDto) {
        return findByAnswerId(id).update(loginUser, new Answer(loginUser, findById(answerDto.getQuestionId()), answerDto.getContents()));
    }

    @Transactional
    public void deleteQuestion(User loginUser, long questionId) {
        Question original = findById(questionId);
        original.delete(loginUser);
        deleteHistoryService.save(new DeleteHistory(ContentType.QUESTION, original.getId(), loginUser, LocalDateTime.now()));
    }

    @Transactional
    public Answer addAnswer(User loginUser, AnswerDto answerDto) {
        return create(loginUser, answerDto);
    }

    private boolean isDeleted(Question question) {
        return Optional.ofNullable(deleteHistoryService.findByContentIdAndContentType(question.getId(), ContentType.QUESTION)).isPresent() || question.isDeleted();
    }

    @Transactional
    public void deleteAnswer(User loginUser, long answerId) {
        Answer answer = findByAnswerId(answerId);
        if (!answer.isOwner(loginUser)) throw new UnAuthorizedException(format(DIFFERENT_OWNER,"답변"));

        answer.delete();
        deleteHistoryService.save(new DeleteHistory(ContentType.ANSWER, answer.getId(), loginUser, LocalDateTime.now()));
    }

    private boolean isDeleted(Answer answer) {
        return Optional.ofNullable(deleteHistoryService.findByContentIdAndContentType(answer.getId(), ContentType.ANSWER)).isPresent() || answer.isDeleted();
    }
}