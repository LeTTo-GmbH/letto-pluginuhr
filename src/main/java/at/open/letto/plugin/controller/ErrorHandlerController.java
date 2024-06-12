package at.open.letto.plugin.controller;

import at.open.letto.plugin.config.Endpoint;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
public class ErrorHandlerController implements ErrorController {

    /** Error Attributes in the Application */
    private ErrorAttributes errorAttributes;

    /** Error Endpoint */
    private final static String ERROR_PATH = Endpoint.error;

    public ErrorHandlerController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

//    @Override
//    public String getErrorPath() {
//        return ERROR_PATH;
//    }

    @RequestMapping(value = ERROR_PATH)
    public String getErrorPath(HttpServletRequest request, WebRequest webRequest, Model model) {
        Map<String, Object> errorAttributes = this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
        model.addAttribute("msg",errorAttributes);
        model.addAttribute("status",getStatus(request));
        return "error";
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equals(parameter.toLowerCase());
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode != null) {
            try {
                return HttpStatus.valueOf(statusCode);
            }
            catch (Exception ex) {
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

}