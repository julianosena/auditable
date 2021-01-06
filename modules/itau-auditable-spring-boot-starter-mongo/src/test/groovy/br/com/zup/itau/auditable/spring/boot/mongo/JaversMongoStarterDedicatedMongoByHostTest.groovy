package br.com.zup.itau.auditable.spring.boot.mongo

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("dedicated-mongo-host")
class ItauAuditableMongoStarterDedicatedMongoByHostTest extends ItauAuditableMongoStarterDedicatedMongoTest {

    def "should read dedicated mongo configuration from host"(){
        expect:
        itauAuditableProperties.mongodb
        itauAuditableProperties.mongodb.host == 'localhost'
        itauAuditableProperties.mongodb.port == ItauAuditableMongoStarterDedicatedMongoTest.PORT
        itauAuditableProperties.mongodb.database == 'itau-auditable-dedicated'
    }
}
