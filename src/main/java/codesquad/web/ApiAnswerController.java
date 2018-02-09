package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
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
@RequestMapping(value = "/api/answers", produces = "application/json")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnAService qnAService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser,
                                       @Valid @RequestBody AnswerDto answerDto) throws CannotDeleteException {
        Answer answer = qnAService.addAnswer(loginUser, answerDto);

        HttpHeaders headers = HtmlFormDataBuilder.defaultHeaders();
        headers.setLocation(URI.create("/api/answers/" + answer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public AnswerDto show(@PathVariable long id) {
        Answer answer = qnAService.findByAnswerId(id);
        return answer.toAnswerDto();
    }

    @PutMapping("/{id}")
    public AnswerDto update(@LoginUser User loginUser,
                              @PathVariable long id,
                              @Valid @RequestBody AnswerDto answerDto) {

        return qnAService.update(loginUser, id, answerDto).toAnswerDto();
    }
}
