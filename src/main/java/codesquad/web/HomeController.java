package codesquad.web;

import codesquad.domain.Paging;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class HomeController {

    @Autowired
    QnaService qnaService;

    private static final Logger logger = getLogger(HomeController.class);

    @GetMapping(path = {"/", "/{no}"})
    public String home(@PathVariable(required = false) Long no,  Model model, HttpSession httpSession) {
        logger.debug("sesseion id : {} ", httpSession.getId());
        Paging paging = new Paging(no);
        model.addAttribute("questions", qnaService.findByPaging(paging));
        model.addAttribute("paging", qnaService.obtainPaging(paging));
        return "home";
    }
}
