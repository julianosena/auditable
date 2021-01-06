package br.com.zup.itau.auditable.spring.jpa

import br.com.zup.itau.auditable.hibernate.entity.Person
import br.com.zup.itau.auditable.hibernate.entity.PersonCrudRepository

import javax.transaction.Transactional

class ErrorThrowingService {
    private PersonCrudRepository repository

    ErrorThrowingService(PersonCrudRepository repository) {
        this.repository = repository
    }

    @Transactional
    void saveAndThrow(Person person) {
        repository.save(person)
        throw new RuntimeException("rollback")
    }
}
