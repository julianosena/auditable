package br.com.zup.itau.auditable.repository.api

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.object.CdoSnapshot
import br.com.zup.itau.auditable.core.model.SnapshotEntity
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class PreviousSnapshotsCalculatorTest extends Specification {

    def "should calculate previous snapshots identifiers and should fetch missing snapshots"() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        def snapshotsA = [""]
        def snapshotsB = [""]
        (0..11).each {
            snapshotsA << itauAuditable.commit("author", new SnapshotEntity(id: 1, intProperty: it)).snapshots[0]
            snapshotsB << itauAuditable.commit("author", new SnapshotEntity(id: 2, intProperty: it)).snapshots[0]
        }

        def snapshotProviderStub = { identifiers ->
            identifiers.collect {
                if (it.globalId.cdoId == 1) return snapshotsA[(int) it.version]
                else return snapshotsB[(int) it.version]
            }
        }

        CdoSnapshot.metaClass.eq = {
            it.globalId == delegate.globalId && it.version == delegate.version
        }

        def calculator = new PreviousSnapshotsCalculator(snapshotProviderStub)

        when:
        def selectedSnapshots = snapshotsA[10, 9, 2] + snapshotsB[10, 9, 2]
        def outputMap = calculator.calculate(selectedSnapshots)


        then:
        outputMap.get(id(snapshotsA[9])) eq snapshotsA[9]
        outputMap.get(id(snapshotsA[8])) eq snapshotsA[8]
        outputMap.get(id(snapshotsA[1])) eq snapshotsA[1]
        outputMap.get(id(snapshotsB[9])) eq snapshotsB[9]
        outputMap.get(id(snapshotsB[8])) eq snapshotsB[8]
        outputMap.get(id(snapshotsB[1])) eq snapshotsB[1]
    }

    def id(CdoSnapshot s) {
        new SnapshotIdentifier(s.globalId, s.version)
    }
}
