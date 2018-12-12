package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
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
@RequestMapping("/api/questions")
public class ApiQnaController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Question question, @LoginUser User loginUser) {
        Question savedQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/" + savedQuestion.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findById(id);
    }

    @PutMapping("/{id}")
    public Question update
            (@PathVariable long id, @LoginUser User loginUser, @Valid @RequestBody Question updatedQuestion) {
        return qnaService.update(loginUser, id, updatedQuestion);
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity<Void> createAnswer(@Valid @RequestBody String contents, @LoginUser User loginUser, @PathVariable long id) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, id, contents);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/" + savedAnswer.generateUrl()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{questionId}/answers/{answerId}")
    public Answer showAnswer(@PathVariable long questionId, @PathVariable long answerId) {
        return qnaService.findByAnswerId(answerId);
    }

    @PutMapping("/{questionId}/answers/{answerId}")
    public Answer updateAnswer(@PathVariable long answerId, @LoginUser User loginUser, @Valid @RequestBody String contents) {
        return qnaService.updateAnswer(loginUser, answerId, contents);
    }

}
