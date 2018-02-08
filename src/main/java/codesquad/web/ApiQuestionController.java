package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/{id}")
    public Question findDetail(@PathVariable Long id) {

        return qnaService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Void> write(
            @LoginUser User user
            , @RequestBody QuestionDto questionDto
    ) {
        Question question = qnaService.create(user, questionDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + question.getId()));

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }


    @PutMapping("/{id}")
    public void update(@LoginUser User user
            , @PathVariable("id") long id
            , @RequestBody QuestionDto questionDto
    ) throws UnAuthenticationException {

        qnaService.update(user, id, questionDto);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @LoginUser User user
            , @PathVariable("id") long id
    ) throws CannotDeleteException {

        qnaService.deleteQuestion(user, id);
    }

}
