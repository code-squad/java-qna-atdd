package codesquad.web;

import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {

    @Autowired
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable Long questionId, @RequestBody String contents) {
        Long id = qnaService.addAnswer(loginUser, questionId, contents).getId();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/api/questions/"+ questionId + "/answers/" + id));
        return new ResponseEntity<Void>(httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id) {
        return qnaService.findAnswerById(id).getContents();
    }

    @PutMapping("/{id}/form")
    public String update(@LoginUser User loginUser, @PathVariable Long id, @RequestBody String contents) throws IllegalAccessException {
        return qnaService.updateAnswer(loginUser, id, contents).getContents();
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@LoginUser User loginUser, @PathVariable Long id) throws IllegalAccessException {
        return qnaService.deleteAnswer(loginUser, id);
    }
}
