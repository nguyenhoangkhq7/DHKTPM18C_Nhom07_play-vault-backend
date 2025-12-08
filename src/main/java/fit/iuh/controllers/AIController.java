package fit.iuh.controllers;



import fit.iuh.services.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class AIController {
    @Autowired
    private AIService aiService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> simpleChat(@RequestParam(value = "request") String request){
        return aiService.chatWithTool(request);
    }
}
