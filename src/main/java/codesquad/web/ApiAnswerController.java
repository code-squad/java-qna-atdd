package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.helper.ApiResponse;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/questions/{questionNo}/answers")
public class ApiAnswerController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @PostMapping("")
    public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionNo
            , @Valid @RequestBody AnswerDto answerDto) {
        Answer savedAnswer = qnaService.addAnswer(loginUser, questionNo, answerDto.getContents());

        return ApiResponse.CREATED(savedAnswer.generateApiUrl());
    }

    @GetMapping("/{answerNo}")
    public AnswerDto show(@PathVariable long answerNo) {
        Answer answer = qnaService.findAnswerById(answerNo);

        return answer.toAnswerDto();
    }

    @PutMapping("/{answerNo}")
    public ResponseEntity<Void> update(@LoginUser User loginUser, @PathVariable long answerNo
            , @Valid @RequestBody AnswerDto answerDto) {
        qnaService.updateAnswer(loginUser, answerNo, answerDto);

        return ApiResponse.OK();
    }

    @DeleteMapping("/{answerNo}")
    public ResponseEntity<Void> delete(@LoginUser User loginUser, @PathVariable long answerNo) {
        qnaService.deleteAnswer(loginUser, answerNo);

        return ApiResponse.OK();
    }
}
