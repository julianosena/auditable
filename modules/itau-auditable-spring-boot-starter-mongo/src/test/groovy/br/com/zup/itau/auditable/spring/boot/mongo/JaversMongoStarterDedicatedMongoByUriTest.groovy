package br.com.zup.itau.auditable.spring.boot.mongo

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("dedicated-mongo-uri")
class ItauAuditableMongoStarterDedicatedMongoByUriTest extends ItauAuditableMongoStarterDedicatedMongoTest {

    def "should read dedicated mongo configuration from URI"(){
        expect:
        javersProperties.mongodb
        javersProperties.mongodb.uri == "mongodb://localhost:${ItauAuditableMongoStarterDedicatedMongoTest.PORT}/itau-auditable-dedicated"
    }
}
