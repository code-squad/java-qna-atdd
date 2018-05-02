package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.CannotUpdateException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(ApiQuestionController.BASE_URL)
public class ApiQuestionController {
	public static final String BASE_URL = "/api/questions";
	
	@Resource(name = "qnaService")
	private QnaService qnaService;
	
	@PostMapping("")
	public ResponseEntity<?> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto) {
		Question saveQuestion = qnaService.createQuestion(loginUser, questionDto.toQuesiton());
		return getResponseEntityWithNextURI(concatURIWithDelimiter(BASE_URL, saveQuestion.getId()), HttpStatus.CREATED);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> get(@PathVariable long id) {
		return ResponseEntity.ok(qnaService.findQuestionById(id)
				.toQuestionDto());
	}
	
	@GetMapping("")
	public ResponseEntity<?> list(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		return ResponseEntity.ok(qnaService.findListQuestionNotDeleted(new PageRequest(pageNumber, pageSize)).stream()
				.map(Question::toQuestionDto)
				.collect(toList()));
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody QuestionDto questionDto) throws CannotUpdateException {
		qnaService.updateQuestion(loginUser, id, questionDto.toQuesiton());
		return getResponseEntityWithNextURI(concatURIWithDelimiter(BASE_URL, id), HttpStatus.OK);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
		qnaService.deleteQuestion(loginUser, id);
		return getResponseEntityWithNextURI(concatURIWithDelimiter(BASE_URL, id), HttpStatus.OK);
	}
	
	@PostMapping("{id}/answers")
	public ResponseEntity<?> addAnswer(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody AnswerDto answerDto) {
		Answer saveAnswer = qnaService.addAnswer(loginUser, id, answerDto.getContents());
		return getResponseEntityWithNextURI(concatURIWithDelimiter(ApiAnswerController.BASE_URL, saveAnswer.getId()), HttpStatus.CREATED);
	}
	
	@GetMapping("{id}/answers")
	public ResponseEntity<?> listAnswer(@PathVariable long id, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
		List<Answer> answers = qnaService.findQuestionById(id)
				.getAnswers();
		
		return ResponseEntity.ok(answers.stream()
				.skip(pageNumber * pageSize)
				.limit(pageSize)
				.map(Answer::toAnswerDto)
				.collect(toList()));
	}
	
	private String concatURIWithDelimiter(Object... resources) {
		return Arrays.stream(resources)
				.map(Object::toString)
				.collect(joining("/"));
	}
	
	private ResponseEntity<?> getResponseEntityWithNextURI(String nextURI, HttpStatus httpStatus) {
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(nextURI));
		return new ResponseEntity<>(headers, httpStatus);
	}
}
