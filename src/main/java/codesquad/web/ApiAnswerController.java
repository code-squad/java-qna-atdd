package codesquad.web;

import java.net.URI;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import codesquad.domain.Answer;
import codesquad.domain.AnswerRepository;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;

@RestController
@RequestMapping("/api/questions")
public class ApiAnswerController {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

	@Resource(name = "qnaService")
	private QnaService qnaService;
	
	@PostMapping("/{questionId}/answers")
	public ResponseEntity<Void> create(@LoginUser User loginUser, @PathVariable long questionId, @Valid @RequestBody Answer answer) {
		log.debug("fuck");
		Question question = qnaService.findById(questionId);
		Answer newAnswer = qnaService.addAnswer(loginUser, questionId, answer.getContents());
		log.debug("Question is " + question.toString());
		log.debug("New Answer is" + newAnswer.toString());
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create("/api/questions/" + question.getId()));
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}
	
	@PutMapping("/{questionId}/answers/{answerId}")
	public Answer update(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long answerId, @RequestBody String contents) throws CannotDeleteException {
		Question question = qnaService.findById(questionId);
		Answer existingAnswer = question.getAnswer(answerId);
			
		Answer updatedAnswer = qnaService.updateAnswer(loginUser, answerId, contents);
		
		log.debug("Question is " + question.toString());
		log.debug("Updated Answer is" + updatedAnswer.toString());
		return updatedAnswer;
	}
	
	@GetMapping("/{questionId}/answers/{answerId}")
	public Answer getAnswer(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long answerId) {
		Question question = qnaService.findById(questionId);
		Answer updatedAnswer = question.getAnswer(answerId);
		
		return updatedAnswer;
	}
	
	@PostMapping("/{questionId}/answers/{answerId}")
	public Answer delete(@LoginUser User loginUser, @PathVariable long questionId, @PathVariable long answerId) throws CannotDeleteException {
		Question question = qnaService.findById(questionId);
		Answer existingAnswer = question.getAnswer(answerId);
		
		Answer deletedAnswer = qnaService.deleteAnswer(loginUser, answerId);
		
		log.debug("Question is " + question.toString());
		log.debug("Deleted Answer is" + existingAnswer.toString());
		log.debug("Answer state is " + existingAnswer.isDeleted());
		
		return deletedAnswer;
	}
}
