package fit.iuh.controllers;

import fit.iuh.dtos.PublisherRequestDto;
import fit.iuh.mappers.PublisherRequestMapper;
import fit.iuh.models.enums.RequestStatus;
import fit.iuh.services.PublisherRequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publisher-requests")
@AllArgsConstructor
public class PublisherRequestController {
    private final PublisherRequestService publisherRequestService;
    private final PublisherRequestMapper publisherRequestMapper;
    @GetMapping("/{username}")
    public ResponseEntity<PublisherRequestDto> publisherRequest(@PathVariable String username) {
        return publisherRequestService.getPublisherRequestByUserName(username)
                .map(req -> ResponseEntity.ok(publisherRequestMapper.toPublisherRequestDto(req)))
                .orElseGet(() -> ResponseEntity.ok(null));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<PublisherRequestDto> approveRequest(@PathVariable Long id) {
        return publisherRequestService.updateStatus(id, RequestStatus.APPROVED)
                .map(req -> ResponseEntity.ok(publisherRequestMapper.toPublisherRequestDto(req)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }



}
