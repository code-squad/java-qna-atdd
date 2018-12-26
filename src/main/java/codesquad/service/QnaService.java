package codesquad.service;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.PagedResources;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service("qnaService")
public class QnaService {
    private static final Logger log = LoggerFactory.getLogger(QnaService.class);

    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @Resource(name = "answerRepository")
    private AnswerRepository answerRepository;

    @Resource(name = "deleteHistoryService")
    private DeleteHistoryService deleteHistoryService;

    public Question createQuestion(User loginUser, Question question) {
        question.writeBy(loginUser);
        log.debug("question : {}", question);
        return questionRepository.save(question);
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    public Optional<Answer> findByIdAnswer(long id) {
        return answerRepository.findById(id);
    }

    @Transactional
    public Question update(User loginUser, Long id, Question updatedQuestion)  {
        Question question = questionRepository.findById(id).orElse(null).updateQuestion(loginUser, updatedQuestion);
        return question;
    }

    @Transactional
    public Question deleteQuestion(User loginUser, long questionId) {
        return questionRepository.findById(questionId).orElse(null)
                .deleteQuestion(loginUser);
    }

    public Iterable<Question> findAll() {
        return questionRepository.findByDeleted(false);
    }

    public Page<Question> findByPaging(Paging paging) {
        return questionRepository.findByDeleted(false, PageRequest.of(paging.getPageNo() - 1, Paging.COUNT_OF_PAGING_CONTENTS));
    }

    public int obtainCountOfQuestion() {
        return (int)questionRepository.findByDeleted(false).stream().count();
    }

    public List<Question> findAll(Pageable pageable) {
        return questionRepository.findAll(pageable).getContent();
    }

    public boolean isSaved(Long id) {
        return answerRepository.findById(id).orElse(null) != null;
    }

    @Transactional
    public Question addAnswer(long questionId, Answer answer) {
        log.debug("Call addAnswer Method!");
        Question question = questionRepository.getOne(questionId);
        question.addAnswer(answer);
        return question;
    }

    @Transactional
    public Question deleteAnswer(User loginUser, long questionId, Answer answer) {
        Question question = questionRepository.getOne(questionId);
        /* 피드백1) 답변삭제를 Question
            --> Answer로 변경 (Question의 answers에서 작업하는 것이 아니라 Answer에서 deleted값만 변경하면되기
                때문에 Answer에 적용하는 것이 맞음
        */
        answer.deleteAnswer(loginUser);
        return question;
    }

    public Paging obtainPaging(Paging paging) {
        return paging.of(questionRepository.findByDeleted(false).size());
    }
}

