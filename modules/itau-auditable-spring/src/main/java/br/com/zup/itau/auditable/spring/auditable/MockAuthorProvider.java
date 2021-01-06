package br.com.zup.itau.auditable.spring.auditable;

public class MockAuthorProvider implements AuthorProvider {
    @Override
    public String provide() {
        return "unknown";
    }
}
