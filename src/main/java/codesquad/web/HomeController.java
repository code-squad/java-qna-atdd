package codesquad.web;

import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

import static org.slf4j.LoggerFactory.getLogger;

@Controller
public class HomeController {

    @Autowired
    QnaService qnaService;

    private static final Logger logger = getLogger(HomeController.class);


    @GetMapping("/")
    public String home(Model model, HttpSession httpSession) {
        logger.debug("sesseion id : {} ", httpSession.getId());
        model.addAttribute("questions", qnaService.findAll());
        return "home";
    }
}
