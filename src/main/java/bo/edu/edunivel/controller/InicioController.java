package bo.edu.edunivel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/health")
    @ResponseBody
    public Map<String, String> health() {
        return Map.of("estado", "ok", "aplicacion", "EduNivel");
    }
}
