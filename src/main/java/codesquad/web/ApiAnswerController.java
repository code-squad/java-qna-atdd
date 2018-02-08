package codesquad.web;

import codesquad.CannotAddException;
import codesquad.CannotDeleteException;
import codesquad.CannotUpdateException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class ApiAnswerController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/{id}")
    public Answer findById(@PathVariable Long id) {
        return qnaService.findAnswerById(id);
    }

    @GetMapping("/questions/{id}")
    public List<Answer> findAnswersByQuestionId(@PathVariable Long id) {
        return qnaService.findAnswersByQuestionId(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> add(
            @LoginUser User user
            , @RequestBody String contents
            , @PathVariable("id") Long QuesionID
    ) throws CannotAddException {
        Answer answer = qnaService.addAnswer(user, QuesionID, contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/answers/" + answer.getId()));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public void update(@LoginUser User user
            , @PathVariable("id") long answerID
            , @RequestBody String contents
    ) throws CannotUpdateException {

        qnaService.updateAnswer(user, answerID, contents);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @LoginUser User user
            , @PathVariable("id") long id
    ) throws CannotDeleteException {

        qnaService.deleteAnswer(user, id);
    }

}
