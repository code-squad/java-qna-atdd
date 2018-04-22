package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QnaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    QnaService qnaService;

    @GetMapping("/")
    public String home(Model model,
                       @PageableDefault(
                               size = 15,
                               sort = "id",
                               direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Question> page = qnaService.findAll(pageable);
        model.addAttribute("questions", page.getContent());

        return "home";
    }
}
