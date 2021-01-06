package br.com.zup.itau.auditable.hibernate.integration

import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import br.com.zup.itau.auditable.core.ItauAuditable
import br.com.zup.itau.auditable.hibernate.entity.Author
import br.com.zup.itau.auditable.hibernate.entity.AuthorCrudRepository
import br.com.zup.itau.auditable.hibernate.entity.Ebook
import br.com.zup.itau.auditable.hibernate.entity.EbookCrudRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = ItauAuditableBeanHibernateProxyConfig)
class ObjectAccessHookBeanSpec extends Specification {

    @Autowired
    ItauAuditable javers

    @Autowired
    EbookCrudRepository ebookRepository

    @Autowired
    AuthorCrudRepository authorRepository

    def "should unproxy hibernate entity with Bean MappingType and commit it to ItauAuditable"() {
        given:
        def author = new Author("1", "George RR Martin")
        authorRepository.save(author);
        def ebook = new Ebook("1", "Throne of Games", author, ["great book"])
        ebookRepository.save(ebook)

        def book = ebookRepository.getOne("1")
        assert book.author instanceof HibernateProxy
        assert !Hibernate.isInitialized(book.author)

        when:
        book.author.name = "kazik"
        ebookRepository.save(book.author)

        then:
        def snapshot = javers.getLatestSnapshot("1", Author).get()
        snapshot.getPropertyValue("name") == "kazik"
    }
}