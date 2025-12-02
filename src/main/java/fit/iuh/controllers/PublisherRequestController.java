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

    @PutMapping("/{id}/{action:approve|reject}")
    public ResponseEntity<PublisherRequestDto> updateRequestStatus(
            @PathVariable Long id,
            @PathVariable String action) {

        RequestStatus status = switch (action.toUpperCase()) {
            case "APPROVE" -> RequestStatus.APPROVED;
            case "REJECT"  -> RequestStatus.REJECTED;
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        };

        return publisherRequestService.updateStatus(id, status)
                .map(publisherRequestMapper::toPublisherRequestDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
