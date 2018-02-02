package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.dto.QuestionsDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/questions/{questionId}/answers")
public class ApiAnswerController {
	private static final Logger log = LoggerFactory.getLogger(ApiAnswerController.class);

	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping("")
	public ResponseEntity<Void> create(@PathVariable long questionId, @LoginUser User loginUser, @Valid @RequestBody AnswerDto answerDto) {
		Answer savedAnswer = qnaService.addAnswer(loginUser, questionId, answerDto.toAnswer());

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(savedAnswer.generateApiUrl()));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public AnswerDto show(@PathVariable long questionId, @PathVariable long id) {
		Answer answer = qnaService.findAnswerById(id);
		log.debug("answer: {}", answer);
		return answer.toAnswerDto().orElse(null);
	}

	@DeleteMapping("/{id}")
	public void delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long id) {
		try {
			qnaService.deleteAnswer(loginUser, id);
		} catch (CannotDeleteException e) {
			log.info(e.getMessage());
		}
	}
}
