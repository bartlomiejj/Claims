package cv.claims.infrastructure.db

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ClaimTestRepository {

    @Autowired
    ClaimEntityRepository claimEntityRepository

    def cleanup() {
        claimEntityRepository.deleteAll()
    }

    def findAll() {
        claimEntityRepository.findAll()
    }
}
