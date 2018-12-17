package codesquad.web;

import codesquad.domain.QuestionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Controller
public class HomeController {
    @Resource(name = "questionRepository")
    private QuestionRepository questionRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("questions", questionRepository.findByDeleted(false));
        return "home";
    }
}
