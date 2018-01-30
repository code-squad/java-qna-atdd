package codesquad.web.api;

import codesquad.CannotManageException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiAnswerController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> getAnswer(@PathVariable Long questionId, @PathVariable Long answerId) throws CannotManageException {
        return ResponseEntity.ok(qnaService.findOneAnswer(answerId));
    }

    @PostMapping("/{questionId}/answers")
    public ResponseEntity<?> postAnswer(@PathVariable Long questionId, @Valid @RequestBody AnswerDto answerDto, @LoginUser User loginUser) throws CannotManageException {
        Answer answer = qnaService.addAnswer(loginUser, questionId, answerDto.getContents());

        return new ResponseEntity<>(answerHttpHeaders(answer.getQuestion().getId(), answer.getId()), HttpStatus.CREATED);
    }

    @PutMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> putAnswer(@PathVariable Long questionId, @PathVariable Long answerId, @RequestBody AnswerDto answerDto, @LoginUser User loginUser) throws CannotManageException {
        Answer answer = qnaService.updateAnswer(loginUser, answerId, Answer.convert(loginUser, answerDto.getContents()));

        return new ResponseEntity<>(answerHttpHeaders(questionId, answer.getId()), HttpStatus.OK);
    }

    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<?> deleteAnswer(@PathVariable Long questionId, @PathVariable Long answerId, @LoginUser User loginUser) throws CannotManageException {
        qnaService.deleteAnswer(loginUser, answerId);

        return new ResponseEntity<>(answerHttpHeaders(questionId, answerId), HttpStatus.OK);
    }

    private HttpHeaders answerHttpHeaders(long questionId, long answerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + answerId));
        return headers;
    }
}
