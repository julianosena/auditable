package br.com.zup.itau.auditable.repository.mongo.cases

import com.mongodb.client.MongoClient

import static br.com.zup.itau.auditable.repository.jql.QueryBuilder.byInstanceId

import javax.persistence.Id

import br.com.zup.itau.auditable.core.ItauAuditableBuilder
import br.com.zup.itau.auditable.repository.mongo.EmbeddedMongoFactory
import br.com.zup.itau.auditable.repository.mongo.MongoRepository

import com.mongodb.client.MongoDatabase

import groovy.transform.EqualsAndHashCode
import spock.lang.Shared
import spock.lang.Specification

/**
 * Map Dot Replacer Test
 *
 * @author luca010
 */
class MapKeyDotReplacerTest extends Specification {

	@Shared def embeddedMongo = EmbeddedMongoFactory.create()
	@Shared MongoClient mongoClient

	def setupSpec() {
		mongoClient = embeddedMongo.getClient()
	}

	void cleanupSpec() {
		embeddedMongo.stop()
	}

	@EqualsAndHashCode
	class SampleDoc {
		@Id
		int id = 1

		Map<String, Integer> state
	}

	def "should commit and read snapshot of Entity containing state field with dot keys"() {
		given:
		MongoDatabase mongo = mongoClient.getDatabase("test")

		def itauAuditable = ItauAuditableBuilder.itauAuditable()
				.registerItauAuditableRepository(new MongoRepository(mongo))
				.build()
		def cdo = new SampleDoc(id: 1, state: ['key.test1': 1, 'key.test2': 2])

		when:
		itauAuditable.commit('author', cdo)
		def snapshots = itauAuditable.findSnapshots(byInstanceId(1, SampleDoc).build())

		then:
		snapshots[0].getPropertyValue('state') == ['key.test1': 1, 'key.test2': 2]
	}
}
