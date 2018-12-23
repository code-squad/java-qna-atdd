package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

@RequestMapping("/api/questions/{questionId}/answers")
@RestController
public class ApiAnswerController {
    private static final Logger logger = getLogger(ApiAnswerController.class);

    @Autowired
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody String contents) {
        Answer createAnswer = qnaService.addAnswer(loginUser, questionId, contents);
        logger.debug("createAnswer : {}", createAnswer);

        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + createAnswer.getId()));

        return new ResponseEntity<>(responseHeader, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Answer> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        return new ResponseEntity<Answer>(qnaService.deleteAnswer(loginUser, id), HttpStatus.OK);
    }
}
