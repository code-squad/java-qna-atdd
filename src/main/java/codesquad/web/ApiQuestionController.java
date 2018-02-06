package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import codesquad.util.UriCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("")
    public List<Question> list() {
         return qnaService.findAll(new PageRequest(0,10));
    }

    @PostMapping("")
    public ResponseEntity<Void> create(@Valid @RequestBody QuestionDto questionDto, @LoginUser User loginUser) {
        Question question = qnaService.create(loginUser, new Question(questionDto.getTitle(), questionDto.getContents()));
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(UriCreator.createUri(question));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public QuestionDto get(@PathVariable long id) {
        return qnaService.findById(id).toQuestionDto();
    }

    @PutMapping("{id}")
    public QuestionDto update(@PathVariable long id
            , @Valid @RequestBody QuestionDto questionDto
            , @LoginUser User loginUser) {
        return qnaService.update(loginUser, id, new Question(questionDto.getTitle(), questionDto.getContents())).toQuestionDto();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable long id
            , @LoginUser User loginUser) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(UriCreator.createUri(new Question()));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
