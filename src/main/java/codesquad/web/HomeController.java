package codesquad.web;

import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    QnaService qnaService;

    @GetMapping("")
    public String home(Model model) {
        model.addAttribute("questions", qnaService.findAll());
        return "/home";
    }
}
