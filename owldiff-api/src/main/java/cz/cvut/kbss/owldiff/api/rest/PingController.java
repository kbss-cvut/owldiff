package cz.cvut.kbss.owldiff.api.rest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/ping")
@RestController
public class PingController {

    @GetMapping
    public String ping() {
        return "pong";
    }
}
