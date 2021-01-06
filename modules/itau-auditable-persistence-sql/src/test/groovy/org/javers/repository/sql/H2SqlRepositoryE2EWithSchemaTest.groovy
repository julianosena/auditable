package br.com.zup.itau.auditable.repository.sql

import java.sql.Connection
import java.sql.DriverManager

/**
 * @author Ian Agius
 */
class H2SqlRepositoryE2EWithSchemaTest extends H2SqlRepositoryE2ETest {

    @Override
    Connection createConnection() {
        DriverManager.getConnection(
                "jdbc:h2:mem:itauAuditable;INIT=create schema if not exists itauAuditable")
    }

    @Override
    String getSchema() {
        return "itauAuditable"
    }
}
