package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
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
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody AnswerDto answerDto) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, answerDto.toAnswer());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(answer.generateRestUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<Answer> read(@PathVariable long answerId) {
        Answer answer = qnaService.findAnswerById(answerId);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @PutMapping("/{answerId}")
    public ResponseEntity update(@LoginUser User loginUser, @PathVariable long answerId, @Valid @RequestBody AnswerDto answerDto) {
        Answer updatedAnswer = qnaService.updateAnswer(loginUser, answerId, answerDto);
        return new ResponseEntity<>(updatedAnswer, HttpStatus.OK);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity delete(@LoginUser User loginUser, @PathVariable long answerId) {
        qnaService.deleteAnswer(loginUser, answerId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
