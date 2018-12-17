package codesquad.web;

import codesquad.service.QnaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Controller
public class HomeController {

    @Resource(name = "qnaService")
    QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("questions",qnaService.findAll());
        return "home";
    }
}
