package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

class Employee {
    @Id
    int id
    Person person
}

class Person {
    @Id
    String name
    Employee employee
}

/**
 * @author bartosz.walacik
 */
class OrganizationStructureLoopCase extends Specification{

    def "should manage Employee to Person Type cycle"(){
        given:
        def person = new Person(name:"kaz")
        def emp = new Employee(id:1, person:person)
        person.employee = emp

        when:
        def itauAuditable = ItauAuditableBuilder.itauAuditable().build()
        itauAuditable.commit("a",emp)

        def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstanceId(1, Employee).build())

        then:
        snapshots.size() == 1
    }
}
