package br.com.zup.itau.auditable.spring.boot.mongo

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("dedicated-mongo-host")
class ItauAuditableMongoStarterDedicatedMongoByHostTest extends ItauAuditableMongoStarterDedicatedMongoTest {

    def "should read dedicated mongo configuration from host"(){
        expect:
        javersProperties.mongodb
        javersProperties.mongodb.host == 'localhost'
        javersProperties.mongodb.port == ItauAuditableMongoStarterDedicatedMongoTest.PORT
        javersProperties.mongodb.database == 'itau-auditable-dedicated'
    }
}
