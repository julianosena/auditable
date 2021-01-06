package br.com.zup.itau.auditable.repository.mongo

class EmbeddedMongoE2EWithRandomGeneratorTest extends EmbeddedMongoE2ETest {

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }
}
