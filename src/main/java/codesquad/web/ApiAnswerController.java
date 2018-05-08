package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
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
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody AnswerDto newAnswer) {
        final Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, newAnswer.getContents());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api" + savedAnswer.generateUrl()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public AnswerDto show(@PathVariable long questionId, @PathVariable long id) {
        final Answer answer = qnaService.findAnswerById(questionId, id);
        if (answer == null)
            return null;

        return answer.toAnswerDto();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id) {
        qnaService.deleteAnswer(loginUser, questionId, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
