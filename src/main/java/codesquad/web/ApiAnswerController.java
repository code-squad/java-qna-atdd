package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
    private static final Logger logger = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Answer> add(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody AnswerDto answerDto) {
        Answer answer = qnaService.addAnswer(loginUser, questionId, answerDto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setLocation(URI.create("/api/questions/" + questionId + "/answers/" + answer.getId()));
        return new ResponseEntity<>(answer, headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Answer> getAnswer(@PathVariable long id) {
        Answer answer = qnaService.findAnswerById(id);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete(@LoginUser User loginUser, @PathVariable long id) {
        qnaService.deleteAnswer(loginUser, id);
    }
}
