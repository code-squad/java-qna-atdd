package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private QnaService questionService;

    @GetMapping("/")
    public String home(Model model) {
        Iterable<Question> questions = questionService.findAll();
        model.addAttribute("questions", questions);

        return "home";
    }
}
