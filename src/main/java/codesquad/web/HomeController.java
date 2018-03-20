package codesquad.web;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import codesquad.domain.Question;
import codesquad.service.QnaService;

@Controller
public class HomeController {
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);

	
	@Resource(name = "qnaService")
	private QnaService qnaService;
	
    @GetMapping("/")
    public String home(Model model, Pageable pageable) {
    	Iterable<Question> question = qnaService.findAll();
    	model.addAttribute("questions", question);
        return "home";
    }
}
