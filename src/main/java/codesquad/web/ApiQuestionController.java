package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnAService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/api/questions", produces = "application/json")
public class ApiQuestionController {
    @Resource(name = "qnaService")
    private QnAService qnAService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User user,
                                       @Valid @RequestBody QuestionDto questionDto) {
        Question question = qnAService.create(user, questionDto);

        HttpHeaders headers = HtmlFormDataBuilder.defaultHeaders();
        headers.setLocation(URI.create("/api/questions/" + question.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public QuestionDto show(@PathVariable long id) {
        Question question = qnAService.findById(id);
        return question.toQuestionDto();
    }

    @PutMapping("/{id}")
    public QuestionDto update(@LoginUser User loginUser,
                               @PathVariable long id,
                               @Valid @RequestBody QuestionDto questionDto) {

        return qnAService.update(loginUser, id, questionDto).toQuestionDto();
    }
}
