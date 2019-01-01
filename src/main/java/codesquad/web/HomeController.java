package codesquad.web;

import codesquad.domain.Paging;
import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class HomeController {
    private static final Logger log = getLogger(HomeController.class);

    @Resource(name = "qnaService")
    private QnaService qnaService;

    @GetMapping("/")
    public String home( Model model) {
        PageRequest pageRequest = PageRequest.of(0, Paging.MAX_ITEM, new Sort(Sort.Direction.DESC, "id")); //현재페이지, 조회할 페이지수, 정렬정보
        Page<Question> questions = qnaService.findAll(pageRequest);
        model.addAttribute("questions", questions.getContent());

        Paging paging = new Paging(questions.getTotalPages(), questions.getNumber());
        model.addAttribute("page", paging);
        return "home";
    }

    @GetMapping("/{pageNumber}")
    public String page(@PathVariable int pageNumber, Model model) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, Paging.MAX_ITEM, new Sort(Sort.Direction.DESC, "id")); //현재페이지, 조회할 페이지수, 정렬정보
        Page<Question> questions = qnaService.findAll(pageRequest);
        model.addAttribute("questions", questions.getContent());

        Paging paging = new Paging(questions.getTotalPages(), questions.getNumber());
        model.addAttribute("page", paging);
        return "home";
    }
}
