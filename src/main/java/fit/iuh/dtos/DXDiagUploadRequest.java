package fit.iuh.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DXDiagUploadRequest {
    private MultipartFile file;
}