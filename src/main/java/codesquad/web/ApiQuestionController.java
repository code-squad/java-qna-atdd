package codesquad.web;

import java.net.URI;

import javax.annotation.Resource;
import javax.naming.AuthenticationException;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {
	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping("")
	public ResponseEntity<Void> create(@LoginUser User loginUser, @Valid @RequestBody QuestionDto questionDto){
		Question newQuestion = qnaService.create(loginUser, questionDto.toQuestion());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/api/questions/"+newQuestion.getId()));
		return new ResponseEntity<Void>(headers,HttpStatus.CREATED);
	}
	
	@GetMapping("{id}")
	public QuestionDto show(@LoginUser User loginUser, @PathVariable Long id) {
		Question question = qnaService.findById(id).get();
		return question.toQuestionDto();
	}
	
	@PutMapping("{id}")
	public void update(@LoginUser User loginUser, @PathVariable Long id, @Valid @RequestBody QuestionDto updatedQuestion) throws AuthenticationException {
			qnaService.update(loginUser, id, updatedQuestion.toQuestion());
	}	
	
	@DeleteMapping("{id}")
	public void delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
		qnaService.deleteQuestion(loginUser, id);
	}
}
