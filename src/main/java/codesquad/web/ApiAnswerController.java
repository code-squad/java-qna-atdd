package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.net.URI;

@RestController
@RequestMapping("/api/questions/{questionsId}/answers")
public class ApiAnswerController {

    private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionsId, @RequestBody String contents) {
        Answer answer = qnaService.addAnswer(loginUser, questionsId, contents);
        log.debug("contents: {}", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(String.format("/api/questions/%d/answers/%d", questionsId, answer.getId())));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public Answer show(@PathVariable long id){
        return qnaService.findByAnswer(id);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable long id, @LoginUser User loginUser) {
        qnaService.deleteAnswer(loginUser, id);
    }
}
