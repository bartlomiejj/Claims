package cv.claims.adapter.in.csv

import cv.claims.IntegrationTest
import cv.claims.infrastructure.db.ClaimEntity
import cv.claims.infrastructure.db.ClaimTestRepository
import cv.claims.infrastructure.db.ProcessStatus
import org.springframework.beans.factory.annotation.Autowired

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CsvUploadControllerIntegrationTest extends IntegrationTest {

    @Autowired
    ClaimTestRepository claimTestRepository

    def cleanup() {
        claimTestRepository.cleanup()
    }

    def "should upload csv data"() {
        given:
        def csvFile = new File("src/test/resources/claims.csv")

        when:
        uploadCsv(csvFile)

        then:
        def list = claimTestRepository.findAll()
        list.size() == 50
        assertClaimsSaved(list)
    }

    private void assertClaimsSaved(List<ClaimEntity> claims) {
        claims.forEach {
            assert it.getId() != null
            assert it.getClaimId() != null
            assert it.getAmount() != null
            assert it.getDeadline() != null
            assert it.getComplexity() != null
            assert it.getCreatedAt() != null
            assert it.getProcessStatus() == ProcessStatus.UNPROCESSED
            assert it.getProcessedAt() == null

        }
    }

    private void uploadCsv(File file) {
        mockMvc.perform(post("/api/v1/claims/upload/csv")
                .contentType("multipart/form-data")
                .param("file", file.getName())
                .content(file.bytes))
                .andExpect(status().isOk())
                .andReturn()
    }
}
