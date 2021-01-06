package br.com.zup.itau.auditable.core;

import br.com.zup.itau.auditable.common.string.PrettyValuePrinter;
import br.com.zup.itau.auditable.common.validation.Validate;
import br.com.zup.itau.auditable.core.ItauAuditableCoreProperties.PrettyPrintDateFormats;
import br.com.zup.itau.auditable.core.commit.CommitId;
import br.com.zup.itau.auditable.core.diff.ListCompareAlgorithm;

import java.util.function.Supplier;

/**
 * @author bartosz walacik
 */
public class ItauAuditableCoreConfiguration {

    private PrettyValuePrinter prettyValuePrinter = PrettyValuePrinter.getDefault();

    private MappingStyle mappingStyle = MappingStyle.FIELD;

    private ListCompareAlgorithm listCompareAlgorithm = ListCompareAlgorithm.SIMPLE;

    private boolean newObjectsSnapshot = false;

    private CommitIdGenerator commitIdGenerator = CommitIdGenerator.SYNCHRONIZED_SEQUENCE;

    private Supplier<CommitId> customCommitIdGenerator;

    ItauAuditableCoreConfiguration withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);
        this.mappingStyle = mappingStyle;
        return this;
    }

    ItauAuditableCoreConfiguration withCommitIdGenerator(CommitIdGenerator commitIdGenerator) {
        Validate.argumentIsNotNull(commitIdGenerator);
        Validate.argumentCheck(commitIdGenerator != CommitIdGenerator.CUSTOM, "use withCustomCommitIdGenerator(Supplier<CommitId>)");
        this.commitIdGenerator = commitIdGenerator;
        this.customCommitIdGenerator = null;
        return this;
    }

    ItauAuditableCoreConfiguration withCustomCommitIdGenerator(Supplier<CommitId> customCommitIdGenerator) {
        Validate.argumentIsNotNull(customCommitIdGenerator);
        this.commitIdGenerator = CommitIdGenerator.CUSTOM;
        this.customCommitIdGenerator = customCommitIdGenerator;
        return this;
    }

    ItauAuditableCoreConfiguration withNewObjectsSnapshot(boolean newObjectsSnapshot) {
        this.newObjectsSnapshot = newObjectsSnapshot;
        return this;
    }

    ItauAuditableCoreConfiguration withListCompareAlgorithm(ListCompareAlgorithm algorithm) {
        this.listCompareAlgorithm = algorithm;
        return this;
    }

    ItauAuditableCoreConfiguration withPrettyPrintDateFormats(PrettyPrintDateFormats prettyPrintDateFormats) {
        prettyValuePrinter = new PrettyValuePrinter(prettyPrintDateFormats);
        return this;
    }

    public PrettyValuePrinter getPrettyValuePrinter() {
        return prettyValuePrinter;
    }

    public MappingStyle getMappingStyle() {
        return mappingStyle;
    }

    public ListCompareAlgorithm getListCompareAlgorithm() {
        return listCompareAlgorithm;
    }

    public boolean isNewObjectsSnapshot() {
        return newObjectsSnapshot;
    }

    public CommitIdGenerator getCommitIdGenerator() {
        return commitIdGenerator;
    }

    public Supplier<CommitId> getCustomCommitIdGenerator() {
        return customCommitIdGenerator;
    }
}
