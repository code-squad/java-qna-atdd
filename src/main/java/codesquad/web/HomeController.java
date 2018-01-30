package codesquad.web;

import codesquad.service.QnaService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
public class HomeController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("questions", qnaService.findAll());
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "/user/login";
    }
}
