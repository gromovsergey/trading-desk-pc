package app.programmatic.ui.common.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationErrorController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(value = PATH, produces = "application/json")
    public String error() {
        return "";
    }

//    @Override
//    public String getErrorPath() {
//        return PATH;
//    }
}
