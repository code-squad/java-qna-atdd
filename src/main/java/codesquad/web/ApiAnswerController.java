package codesquad.web;

import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

/**
 * Created by hoon on 2018. 2. 7..
 */
@RestController
@RequestMapping("api/questions/{questionIdx}/answers")
public class ApiAnswerController {

    private final QnaService qnaService;

    @Autowired
    public ApiAnswerController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @GetMapping("{idx}")
    public Answer show(@PathVariable Long idx) {
        return qnaService.findAnswerById(idx);
    }

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable(value = "questionIdx")Long questionIdx, @RequestBody String contents) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionIdx, contents);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/questions/" + questionIdx + "/answers/" + savedAnswer.getId()));
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("{idx}")
    public Answer update(@LoginUser User loginUser, @PathVariable Long idx, @RequestBody AnswerDto answerDto) {
        return qnaService.updateAnswer(loginUser, idx, answerDto);
    }

    @DeleteMapping("{idx}")
    public Answer delete(@LoginUser User loginUser, @PathVariable Long idx) {
        return qnaService.deleteAnswer(loginUser, idx);
    }


}
