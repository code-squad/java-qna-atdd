package codesquad.web;

import ch.qos.logback.core.net.SyslogOutputStream;
import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.HttpSessionUtils;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    @PostMapping()
    public ResponseEntity inquire(@LoginUser User loginUser, @Valid @RequestBody Question question, BindingResult result) {
        logger.debug("Call Method!!!" + question);
        HttpHeaders httpHeaders = new HttpHeaders();
        if (result.hasErrors()) {
            logger.debug("유효한 데이터를 입력하지 않아서 예외 발생!");
            return new ResponseEntity(httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        httpHeaders.setLocation(URI.create(String.format("/api/questions/%s", Long.valueOf(qnaService.create(loginUser, question).getId()))));
        return new ResponseEntity(httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public @ResponseBody Question detail(@PathVariable Long id, HttpSession httpSession) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/api/questions/"));
        Question question = qnaService.findById(id).orElse(null)
                                .applyOwner(HttpSessionUtils.getUserFromSession(httpSession));
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("Question", question.toString());
        return question;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@LoginUser User loginUser, @PathVariable Long id)
            throws CannotDeleteException, UnAuthenticationException {
        qnaService.isOneSelfQuestion(loginUser, id);
        qnaService.deleteQuestion(loginUser, id);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/"));
        return new ResponseEntity(httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody Question updatedQuestion) throws UnAuthenticationException {
        logger.debug("Call Method!");
        qnaService.isOneSelfQuestion(loginUser, id);
        Question question = qnaService.update(loginUser, id, updatedQuestion);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(String.format("/api/questions/%s", Long.valueOf(question.getId()))));
        return new ResponseEntity(httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity createAnswer(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody Answer answer,
                                 BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity(new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        qnaService.addAnswer(id, answer);
        return new ResponseEntity(new HttpHeaders(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/answers")
    public ResponseEntity deleteAnswer(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody Answer answer) throws UnAuthenticationException {
        logger.debug("Call updateAnswer Method!");
        qnaService.isOneSelfAnswer(loginUser, answer);
        qnaService.deleteAnswer(id, answer);
        return new ResponseEntity(new HttpHeaders(), HttpStatus.CREATED);
    }
}