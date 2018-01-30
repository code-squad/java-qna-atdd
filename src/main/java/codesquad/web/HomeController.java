package codesquad.web;

import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
public class HomeController {

    @Autowired
    private QnaService qnaService;

    @GetMapping("/")
    public String home(@PageableDefault(sort = "id", direction = DESC) Pageable pageable,
                       Model model) {
        model.addAttribute("questions", qnaService.findAll(pageable).getContent());
        return "home";
    }
}
