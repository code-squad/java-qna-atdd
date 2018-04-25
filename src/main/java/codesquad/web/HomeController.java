package codesquad.web;

import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Pageable pageable, Model model) {
        model.addAttribute("questions", qnaService.findAll(pageable));
        return "home";
    }
}
