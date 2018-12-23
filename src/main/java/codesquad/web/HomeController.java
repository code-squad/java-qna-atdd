package codesquad.web;

import codesquad.domain.Question;
import codesquad.service.QnaService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import support.utils.PagingUtils;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
public class HomeController {
    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home(Model model, Optional<Integer> page) {
        Page<Question> currentPage = qnaService.findAll(page);
        model.addAttribute("questions", currentPage);
        model.addAttribute("pagingUtils", PagingUtils.of(currentPage));
        return "home";
    }
}
