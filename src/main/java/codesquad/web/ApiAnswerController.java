package codesquad.web;

import codesquad.CannotDeleteException;
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
import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/qna/{questionId}/answers")
public class ApiAnswerController {
    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Answer> create(@LoginUser User loginUser, @PathVariable long questionId, @RequestBody Map<String, String> data) {
        log.info("api answer controller loginUser : {}", loginUser);

        Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, data.get("contents"));

        log.info("api answer controller savedAnswer : {}", savedAnswer);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedAnswer.generateUrl()));
        return new ResponseEntity<>(savedAnswer, headers, HttpStatus.CREATED);
    }

    @GetMapping("/{answerId}")
    public AnswerDto show(@PathVariable long answerId) {
        Answer answer = qnaService.findAnswerById(answerId).orElseThrow(EntityNotFoundException::new);
        return answer.toAnswerDto();
    }

    @DeleteMapping("/{answerId}")
    public void delete(@PathVariable long answerId, @LoginUser User loginUser) throws CannotDeleteException {
        qnaService.deleteAnswer(loginUser, answerId);
    }
}
