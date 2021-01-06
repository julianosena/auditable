package br.com.zup.itau.auditable.core;

/**
 * @author bartosz.walacik
 */
public interface ItauAuditableBuilderPlugin {
    void beforeAssemble(ItauAuditableBuilder itauAuditableBuilder);
}
