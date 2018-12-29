package codesquad.web;

import codesquad.CannotDeleteException;
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
public class ApiQuestionController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question newQuestion) {
        Question question = qnaService.create(loginUser, newQuestion);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/api/questions/" + question.getId()));
        return new ResponseEntity<Void>(httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findById(id);
    }

    @PutMapping("/{id}")
    public Question update(@PathVariable long id, @LoginUser User loginUser, @Valid @RequestBody Question updateQuestion) {
        return qnaService.update(loginUser, id, updateQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Question> delete(@PathVariable long id, @LoginUser User loginUser) {
        qnaService.deleteQuestion(loginUser, id);
        return new ResponseEntity<Question>(qnaService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/answers")
    public ResponseEntity createAnswer(@LoginUser User loginUesr, @PathVariable long id, @Valid @RequestBody String contents) {
        qnaService.addAnswer(loginUesr, id, contents);
        return new ResponseEntity(new HttpHeaders(), HttpStatus.CREATED);
    }

    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity deleteAnswer(@PathVariable long questionId, @PathVariable long answerId, @LoginUser User loginUser) throws CannotDeleteException {
//        qnaService.findById(questionId);
        qnaService.deleteAnswer(loginUser, answerId);
        return new ResponseEntity(new HttpHeaders(), HttpStatus.OK);
    }
}
