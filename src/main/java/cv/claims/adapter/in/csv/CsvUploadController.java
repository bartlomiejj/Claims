package cv.claims.adapter.in.csv;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/claims/upload")
@RequiredArgsConstructor
class CsvUploadController {

    private final CsvProcessingService csvProcessingService;

    @PostMapping("/csv")
    public void uploadClaims(@RequestBody byte[] csvFile) {
        csvProcessingService.processCsv(csvFile);
    }
}
