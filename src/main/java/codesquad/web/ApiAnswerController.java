package codesquad.web;


import java.net.URI;

import javax.annotation.Resource;
import javax.naming.AuthenticationException;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import codesquad.CannotDeleteException;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

@RestController
@RequestMapping("/api/answers")
public class ApiAnswerController {
	@Resource(name = "qnaService")
	private QnaService qnaService;

	@PostMapping("{questionId}")
	public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable Long questionId, @Valid @RequestBody String contents){
		qnaService.addAnswer(loginUser, questionId, contents);
		HttpHeaders headers =  new HttpHeaders();
		headers.setLocation(URI.create("/api/questions/"+questionId));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED); 
	}
	
	@DeleteMapping("{id}")
	public void delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException, AuthenticationException {
		qnaService.deleteAnswer(loginUser, id);
	}
}
