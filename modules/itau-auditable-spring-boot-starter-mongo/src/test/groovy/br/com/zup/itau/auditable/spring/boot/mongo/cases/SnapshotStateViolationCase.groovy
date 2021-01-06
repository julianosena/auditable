package br.com.zup.itau.auditable.spring.boot.mongo.cases

import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.repository.jql.QueryBuilder
import br.com.zup.itau.auditable.spring.ItauAuditableSpringProperties
import br.com.zup.itau.auditable.spring.boot.mongo.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class SnapshotStateViolationCase extends Specification {

	@Autowired ItauAuditable itauAuditable

	@Autowired ItauAuditableSpringProperties itauAuditableProperties

	interface ISimplePojo {
		int getName()

		void setName(int name)
	}

	class SimplePojo implements ISimplePojo {
		int name;

		@Override
		int getName() {
			return name
		}

		@Override
		void setName(int name) {
			this.name = name
		}
	}

	 interface IExtendedPojo extends ISimplePojo {
		int getAnotherName()

		void setAnotherName(int anotherName)
	}

	class ExtendedPojo extends SimplePojo implements IExtendedPojo {
		int anotherName

		int getAnotherName() {
			return anotherName
		}

		void setAnotherName(int anotherName) {
			this.anotherName = anotherName
		}
	}

	def "should expect not to get snapshot violation exception when mappingStyle: bean" () {
		given:
		itauAuditableProperties.mappingStyle == "bean"

		ExtendedPojo ep = new ExtendedPojo()
		ep.setAnotherName(1)
		ep.setName(2)

		when:
		itauAuditable.commit("a", ep)
		def snapshots = itauAuditable.findSnapshots(QueryBuilder.byInstance(ep).build())
		
		then:
		assert snapshots.size() == 1
	}
}
