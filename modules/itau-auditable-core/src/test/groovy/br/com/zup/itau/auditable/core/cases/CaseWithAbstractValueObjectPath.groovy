package br.com.zup.itau.auditable.core.cases

import br.com.zup.itau.auditable.core.Changes
import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.core.diff.changetype.ValueChange
import br.com.zup.itau.auditable.core.metamodel.annotation.Entity
import br.com.zup.itau.auditable.core.metamodel.annotation.Id
import br.com.zup.itau.auditable.repository.inmemory.InMemoryRepository
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * see https://stackoverflow.com/questions/51634751/itauAuditableexception-property-not-found-property-in-derived-class-not-found-in-abstr
 */
class CaseWithAbstractValueObjectPath extends Specification {

    @Entity
    class InputForm {
        @Id
        String id
        List<InputFormGroup> inputFormGroups
    }

    abstract class InputFormGroup {
        String id
        String name
    }

    class StaticInputFormGroup extends InputFormGroup {
        InputControl inputControl
    }

    class DynamicInputFormGroup extends InputFormGroup {
        List<InputControl> inputControlList
    }

    class InputControl {
        String value

        InputControl(String value) {
            this.value = value
        }
    }

    def "should manage query for Value Object by concrete path"(){
      given:
      def repo = new InMemoryRepository()
      def itauAuditable = ItauAuditableBuilder.itauAuditable().registerItauAuditableRepository(repo) .build()
      def staticInputFormGroup =
              new StaticInputFormGroup(id: "100", inputControl: new InputControl("static Input"))

      def dynamicInputFormGroup =
              new DynamicInputFormGroup(id: "200", inputControlList: [new InputControl("dynamic Input")])

      def inputForm = new InputForm(id:"inputFormId", inputFormGroups: [staticInputFormGroup, dynamicInputFormGroup])

      when:
      itauAuditable.commit("author", inputForm)

      //Change the value
      dynamicInputFormGroup.inputControlList[0].value = "New Value"

      itauAuditable.commit("author", inputForm)

      //Change the value again
      dynamicInputFormGroup.inputControlList[0].value = "New Value 2"

      itauAuditable.commit("author", inputForm)

      Changes changes = itauAuditable.findChanges(QueryBuilder.byClass(InputForm).withChildValueObjects().build())
      println "all " + changes.prettyPrint()


      def path = changes.find {it instanceof ValueChange}.affectedGlobalId.fragment
      println "query path: " + path

      // new itauAuditable instance - fresh TypeMapper state
      itauAuditable = ItauAuditableBuilder.itauAuditable().registerItauAuditableRepository(repo) .build()

      then:
      // This has thrown
      // ItauAuditableException: PROPERTY_NOT_FOUND: Property 'inputControlList' not found in class 'com.example.itauAuditablepolymorphismissue.model.InputFormGroup'. If the name is correct - check annotations. Properties with @DiffIgnore or @Transient are not visible for JaVers.
      Changes valueChanges = itauAuditable.findChanges(QueryBuilder.byValueObject(InputForm, path).build())
      println valueChanges.prettyPrint()

      valueChanges.size() == 2
    }
}
