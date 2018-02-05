package codesquad.web;

import codesquad.CannotFindException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody String contents) {
        Answer addAnswer = null;
        try {
            addAnswer = qnaService.addAnswer(loginUser, questionId, contents);
        } catch (CannotFindException e) {
            log.error(e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(addAnswer.generateApiUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{answerId}")
    public AnswerDto show(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long answerId) throws CannotFindException {
        return qnaService.findAnswer(questionId, answerId).toAnswerDto();
    }

    @PutMapping("/{answerId}")
    public AnswerDto update(@LoginUser User loginUser, @PathVariable long questionId
            , @PathVariable long answerId, @Valid @RequestBody String contents) throws CannotFindException {
        return qnaService.updateAnswer(loginUser, questionId, answerId, contents).toAnswerDto();
    }

    @DeleteMapping("/{answerId}")
    public void delete(@LoginUser User loginUser, @PathVariable long answerId) throws CannotFindException {
        qnaService.deleteAnswer(loginUser, answerId);
    }
}
