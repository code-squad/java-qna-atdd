package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
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
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Autowired
    private QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @RequestBody QuestionDto questionDto) {
        Question question = new Question(questionDto.getTitle(), questionDto.getContents());
        question = qnaService.create(loginUser, question);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/api/questions/" + question.getId()));
        return new ResponseEntity<Void>(httpHeaders, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public QuestionDto show(@PathVariable Long id) {
        Question question = qnaService.findById(id);
        return question.toQuestionDto();
    }

    @PutMapping("/{id}")
    public QuestionDto update(@LoginUser User loginUser, @PathVariable Long id, @RequestBody QuestionDto questionDto) throws IllegalAccessException {
        Question question = new Question(questionDto.getTitle(), questionDto.getContents());
        return qnaService.update(loginUser, id, question).toQuestionDto();
    }

    @DeleteMapping("/{id}")
    public Boolean delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        return qnaService.deleteQuestion(loginUser, id);
    }
}
