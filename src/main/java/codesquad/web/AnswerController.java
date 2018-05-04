package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.CannotUpdateException;
import codesquad.domain.Answer;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.AnswerDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Controller
@RequestMapping("/answers")
public class AnswerController {
	
	@Resource(name = "qnaService")
	private QnaService qnaService;
	
	@GetMapping("/{id}/form")
	public String updateForm(@LoginUser User loginUser, @PathVariable long id, Model model) {
		Answer answer = qnaService.findAnswerById(id);
		if (answer.isOwner(loginUser)) {
			model.addAttribute("answer", answer);
			return "/qna/answer_updateForm";
		}
		return "/qna/update_failed";
	}
	
	@PutMapping("{id}")
	public String update(@LoginUser User loginUser, @PathVariable long id, AnswerDto target) {
		try {
			qnaService.updateAnswer(loginUser, id, target.toAnswer());
			return "redirect:/questions/" + qnaService.findAnswerById(id).getQuestion().getId();
		} catch (CannotUpdateException e) {
			return "/qna/update_failed";
		}
	}
	
	@DeleteMapping("{id}")
	public String delete(@LoginUser User loginUser, @PathVariable long id) {
		try {
			qnaService.deleteAnswer(loginUser, id);
			return "redirect:/questions/" + qnaService.findAnswerById(id).getQuestion().getId();
		} catch (CannotDeleteException e) {
			return "/qna/delete_failed";
		}
	}
}
