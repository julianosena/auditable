package br.com.zup.itau.auditable.core.changelog

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.model.DummyUser
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class ChangeListTraverserIntegrationTest extends Specification {

    def "should call user's callback methods while iterating over change list "() {
        given:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()

        def user = new DummyUser('bob', 'Dijk')
        itauAuditable.commit("some author", user)

        user.setAge(18)
        user.setSex(DummyUser.Sex.MALE)
        itauAuditable.commit('some author', user)

        itauAuditable.commitShallowDelete('some author', user)
        def callbackMock = Mock(ChangeProcessor)

        when:
        def changes = itauAuditable.findChanges(QueryBuilder.byInstanceId('bob',DummyUser).build())
        itauAuditable.processChangeList(changes, callbackMock)

        then:
        with(callbackMock) {
            1 * beforeChangeList()

            2 * onCommit(_)
            3 * beforeChange(_)
            3 * afterChange(_)

            1 * onObjectRemoved(_)

            1 * onAffectedObject(_)
            2 * onPropertyChange(_)
            2 * onValueChange(_)

            1 * afterChangeList()

            1 * result()

            0 * _ //and no others interactions
        }
    }
}
