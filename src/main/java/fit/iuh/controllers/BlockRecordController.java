package fit.iuh.controllers;

import fit.iuh.dtos.BlockRecordDto;
import fit.iuh.dtos.BlockRequest;
import fit.iuh.dtos.BlockResponse;
import fit.iuh.mappers.BlockRecordMapper;
import fit.iuh.models.BlockRecord;
import fit.iuh.services.BlockRecordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/block-records")
@AllArgsConstructor
public class BlockRecordController {
    private final BlockRecordService blockRecordService;
    private final BlockRecordMapper blockRecordMapper;

    @GetMapping("/{username}")
    public ResponseEntity<List<BlockRecordDto>> getBlockRecord(@PathVariable String username) {
        return ResponseEntity.ok(blockRecordService.getBlockRecordsByUserName(username));
    }

    @PostMapping("/{username}/block")
    public ResponseEntity<BlockResponse> block(
            @PathVariable String username,
            @Valid @RequestBody BlockRequest request) {

        BlockResponse result = blockRecordService.block(username, request);
        return ResponseEntity.ok(result); // 200 OK + body
    }

    @PostMapping("/{username}/unblock")
    public ResponseEntity<BlockResponse> unblock(@PathVariable String username) {

        BlockResponse result = blockRecordService.unblock(username);
        return ResponseEntity.ok(result);
    }

}
