package fit.iuh.controllers;

import fit.iuh.dtos.BlockRecordDto;
import fit.iuh.models.BlockRecord;
import fit.iuh.services.BlockRecordService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/block-records")
@AllArgsConstructor
public class BlockRecordController {
    private final BlockRecordService blockRecordService;
    @GetMapping("/{username}")
    public ResponseEntity<List<BlockRecordDto>> getBlockRecord(@PathVariable String username) {
        return ResponseEntity.ok(blockRecordService.getBlockRecordsByUserName(username));
    }
}
