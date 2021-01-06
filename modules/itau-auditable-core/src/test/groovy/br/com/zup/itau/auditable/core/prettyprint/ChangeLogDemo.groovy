package br.com.zup.itau.auditable.core.prettyprint

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.changelog.SimpleTextChangeLog
import br.com.zup.itau.auditable.core.model.DummyUser
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class ChangeLogDemo extends Specification {

    def "should pretty print the changeLog"() {
        given:
            def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

            def user = new DummyUser('bob', 'Dijk')
            user.setStringSet(['groovy'] as Set)
            itauAuditable.commit("some author", user)

            user.setAge(18)
            user.setSurname('van Dijk')
            user.setSupervisor(new DummyUser('New Supervisor'))
            itauAuditable.commit('some author', user)

            user.setIntegerList([22,23])
            user.setSex(DummyUser.Sex.MALE)
            user.setStringSet(['java','scala'] as Set)
            itauAuditable.commit('another author', user)

            itauAuditable.commitShallowDelete('another author', user)


        when:
            def changes = itauAuditable.findChanges(QueryBuilder.byInstanceId('bob',DummyUser).build())
            def textChangeLog = itauAuditable.processChangeList(changes, new SimpleTextChangeLog())

        then:
            println textChangeLog
            textChangeLog.length() > 0 //it's a demo, not a real test
    }

}
