package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.UnAuthorizedException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
        httpHeaders.setLocation(URI.create("/api/questions/" + Long.valueOf(question.getId())));
        return new ResponseEntity<Void>(httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public Question update(@PathVariable long id, @LoginUser User loginUser, @Valid @RequestBody Question updateQuestion) {
        return qnaService.update(loginUser, id, updateQuestion);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable long id, @LoginUser User loginUser) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PostMapping("{id}/answers")
    public ResponseEntity createAnswer(@PathVariable long id, @LoginUser User loginUesr, @Valid @RequestBody Answer answer, BindingResult bindingResult) {
        // @Valid 에서 에러발생시 BindingResult로 에러값이 들어감
        if(bindingResult.hasErrors()) {
            return new ResponseEntity(new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        qnaService.addAnswer(loginUesr, id, answer);
        return new ResponseEntity(new HttpHeaders(), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}/answers/{answerId}")
    public ResponseEntity deleteAnswer(@PathVariable long id, @PathVariable long answerId, @LoginUser User loginUser) throws CannotDeleteException {
        Question question = qnaService.findById(id).orElseThrow(UnAuthorizedException::new);

        qnaService.deleteAnswer(loginUser, id);
        return new ResponseEntity(new HttpHeaders(), HttpStatus.OK);
    }
}
