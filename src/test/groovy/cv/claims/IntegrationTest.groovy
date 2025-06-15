package cv.claims

import com.fasterxml.jackson.databind.ObjectMapper
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Specification

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class IntegrationTest extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    ObjectMapper objectMapper

    @SpringBean
    RestTemplate restTemplate = Mock(RestTemplate.class)

    def setupSpec() {
        final PostgreSQLContainer postgresContainer = new PostgreSQLContainer(DockerImageName.parse("postgres:latest"))
        postgresContainer.start()
        Integer firstMappedPort = postgresContainer.getMappedPort(5432)
        String user = postgresContainer.getUsername()
        String password = postgresContainer.getUsername()

        System.setProperty("spring.datasource.url", "jdbc:postgresql://localhost:" + firstMappedPort + "/postgres")
        System.setProperty("spring.datasource.username", user)
        System.setProperty("spring.datasource.password", password)

    }

}
