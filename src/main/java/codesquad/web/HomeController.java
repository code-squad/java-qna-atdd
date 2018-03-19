package codesquad.web;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import codesquad.domain.QuestionRepository;
import codesquad.service.QnaService;

@Controller
public class HomeController {
	@Autowired
	private QuestionRepository questionRepository;
	
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("questions", questionRepository.findByDeleted(false));
		return "home";
	}
}
