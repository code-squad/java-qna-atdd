package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.CannotUpdateException;
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
import java.util.Arrays;

import static java.util.stream.Collectors.joining;

@RestController
@RequestMapping(ApiAnswerController.BASE_URL)
public class ApiAnswerController {
	public static final String BASE_URL = "/api/answers";
	
	@Resource(name = "qnaService")
	private QnaService qnaService;
	
	@GetMapping("{id}")
	public ResponseEntity<?> get(@PathVariable long id) {
		return ResponseEntity.ok(qnaService.findAnswerById(id)
				.toAnswerDto());
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> update(@LoginUser User loginUser, @PathVariable long id, @Valid @RequestBody AnswerDto answerDto) throws CannotUpdateException {
		qnaService.updateAnswer(loginUser, id, answerDto.toAnswer());
		return getResponseEntityWithNextURI(concatURIWithDelimiter(BASE_URL, id), HttpStatus.OK);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> delete(@LoginUser User loginUser, @PathVariable long id) throws CannotDeleteException {
		qnaService.deleteAnswer(loginUser, id);
		return getResponseEntityWithNextURI(concatURIWithDelimiter(BASE_URL, id), HttpStatus.OK);
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
