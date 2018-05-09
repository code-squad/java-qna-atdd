package codesquad.web;

import codesquad.domain.User;
import codesquad.dto.AnswerDto;
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
@RequestMapping("/api/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    QnaService qnaService;

    @PostMapping
    public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody AnswerDto answerDto){
        AnswerDto savedAnswer = qnaService.addAnswer(loginUser, answerDto.getQuestionId(), answerDto.getContents()).toAnswerDto();
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/answers/" + savedAnswer.getId()));
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public AnswerDto answer(@PathVariable long id){
        return qnaService.findAnserById(id).toAnswerDto();
    }

    @PutMapping("/{id}")
    public void update(@PathVariable long id, @LoginUser User loginUser, @Valid @RequestBody AnswerDto answerDto){
        qnaService.updateAnswer(loginUser, id, answerDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id, @LoginUser User loginUser){
        qnaService.deleteAnswer(loginUser, id);
    }
}
