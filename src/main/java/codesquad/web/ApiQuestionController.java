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

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody Question question, @LoginUser User loginUser) {
        Question savedQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public Question show(@PathVariable long id) {
        return qnaService.findQuestionById(id).get();
    }

    @PutMapping("/{id}")
    public Question update(@PathVariable long id, @LoginUser User loginUser, @Valid @RequestBody Question updatedQuestion) {
        return qnaService.update(loginUser, id, updatedQuestion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id, @LoginUser User loginUser) {
        try {
            qnaService.deleteQuestion(loginUser, id);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (CannotDeleteException e) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }
    }

}
