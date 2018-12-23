package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.*;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.DeleteHistoryService;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    private static final Logger logger = getLogger(ApiQuestionController.class);

    @Autowired
    private QnaService qnaService;

    @Autowired
    private DeleteHistoryService deleteHistoryService;

    @PostMapping()
    public ResponseEntity createQuestion(@LoginUser User loginUser, @Valid @RequestBody Question question, BindingResult result) {
        logger.debug("Call Method!!!" + question);
        HttpHeaders httpHeaders = new HttpHeaders();
        if (result.hasErrors()) {
            logger.debug("유효한 데이터를 입력하지 않아서 예외 발생!");
            return new ResponseEntity(httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        httpHeaders.setLocation(URI.create(String.format("/api/questions/%s", Long.valueOf(qnaService.createQuestion(loginUser, question).getId()))));
        return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> detailQuestion(@PathVariable Long id, HttpSession httpSession) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/api/questions/"));
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        Question question = qnaService.findById(id).orElse(null)
                .applyOwner(HttpSessionUtils.getUserFromSession(httpSession));
        return new ResponseEntity(question, httpHeaders, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteQuestion(@LoginUser User loginUser, @PathVariable Long id)
            throws CannotDeleteException, UnAuthenticationException {
        Question question = qnaService.deleteQuestion(loginUser, id);
        deleteHistoryService.saveAll(DeleteHistory.createDeleteHistories(question, id));
        return new ResponseEntity(new HttpHeaders(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateQuestion(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody Question updatedQuestion) throws UnAuthenticationException {
        logger.debug("Call Method!");
        Question question = qnaService.update(loginUser, id, updatedQuestion);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(String.format("/api/questions/%s", Long.valueOf(question.getId()))));
        return new ResponseEntity(httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity createAnswer(@LoginUser User loginUser, @PathVariable Long id, @RequestBody String contents,
                                 BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity(new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Question question = qnaService.addAnswer(id, new Answer(loginUser, contents));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(String.format("/api/questions/%s/answers", Long.valueOf(id))));
        logger.debug("After Create Answer, answer location : {} ",httpHeaders.getLocation().getPath());
        return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/answers")
    public ResponseEntity deleteAnswer(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody Answer answer) throws UnAuthenticationException {
        logger.debug("Call updateAnswer Method!");
        qnaService.deleteAnswer(loginUser, id, answer);
        deleteHistoryService.save(new DeleteHistory(ContentType.ANSWER, answer.getId(), loginUser));
        return new ResponseEntity(new HttpHeaders(), HttpStatus.OK);
    }
}