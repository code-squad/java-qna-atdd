package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.UnAuthenticationException;
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
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody Question question) {     //@RequestBody  json->객체로
        Question savedQuestion = qnaService.create(loginUser, question);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + savedQuestion.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
//        return new ResponseEntity<>(headers,HttpStatus.CREATED);  //위와 차이가?
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody Question modifedQuestion) throws UnAuthenticationException {
        Question updatedQuestion = qnaService.update(loginUser, id, modifedQuestion);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + updatedQuestion.getId()));
//        headers.setLocation(URI.create("/api/questions/" + id));  //얘 안되나??????
        return new ResponseEntity<Void>(headers, HttpStatus.OK);
    }

//    @DeleteMapping("{id}")
//    public ResponseEntity<Question> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
//        qnaService.delete(loginUser, id);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(URI.create("/api/questions/" + id));
//        return new ResponseEntity<Question>(headers, HttpStatus.OK);
//    }

    @DeleteMapping("{id}")
    public ResponseEntity<Question> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
        qnaService.delete(loginUser, id);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + id));
        return new ResponseEntity<Question>(qnaService.findById(id).get(), headers, HttpStatus.OK);
    }

//    @PostMapping("")
//    public ResponseEntity<Void> create(@LoginUser User loginUser, String title, String contents) {
//        Question savedQuestion = qnaService.create(loginUser, new Question(title,contents));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setLocation(URI.create("/api/users/" + savedQuestion.getId()));
//        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
//    }
}
