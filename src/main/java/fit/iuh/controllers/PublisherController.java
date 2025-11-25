package fit.iuh.controllers;

import fit.iuh.dtos.PublisherDto;
import fit.iuh.services.PublisherService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@AllArgsConstructor
public class PublisherController {
    private final PublisherService publisherService;
    @GetMapping
    public ResponseEntity<List<PublisherDto>> getPublisher(){

        return ResponseEntity.ok(publisherService.findAll());
    }
}
