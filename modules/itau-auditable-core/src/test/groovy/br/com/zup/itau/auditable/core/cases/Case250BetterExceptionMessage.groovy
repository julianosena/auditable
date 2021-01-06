package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.common.exception.ItauAuditableException
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import java.time.LocalDateTime
import spock.lang.Specification
import spock.lang.Unroll

import static br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode.COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED
import static br.com.zup.itau.auditable.common.exception.ItauAuditableExceptionCode.COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED

/**
 * @author bartosz.walacik
 */
public class Case250BetterExceptionMessage extends Specification {
    @Unroll
    def "should throw nice error message when committing top-level value"() {
        given:
        ItauAuditable javers = ItauAuditableBuilder.javers().build();

        when:
        javers.commit("z",val)

        then:
        ItauAuditableException e = thrown()
        e.code == COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED
        println e.getMessage()

        where:
        val << ["String", 1, LocalDateTime.now()]
    }

    @Unroll
    def "should throw nice error message when comparing top-level values"() {
        given:
        ItauAuditable javers = ItauAuditableBuilder.javers().build();

        when:
        javers.compare(val, val);

        then:
        ItauAuditableException e = thrown()
        e.code == COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED
        println e.getMessage()

        where:
        val << ["String", 1, LocalDateTime.now()]
    }
}
