package br.com.zup.itau.auditable.repository.sql

class H2SqlRepositoryE2EWithRandomGeneratorTest extends H2SqlRepositoryE2ETest {

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }
}
