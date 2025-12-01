package fit.iuh.controllers;

import fit.iuh.dtos.PublisherDto;
import fit.iuh.services.PublisherService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/by-username/{username}/profile")
    public ResponseEntity<PublisherDto> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(publisherService.getProfileByUsername(username));
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<PublisherDto> update(@PathVariable Long id,
                                               @RequestBody PublisherDto dto) {
        return ResponseEntity.ok(publisherService.updateProfile(id, dto));
    }
}
