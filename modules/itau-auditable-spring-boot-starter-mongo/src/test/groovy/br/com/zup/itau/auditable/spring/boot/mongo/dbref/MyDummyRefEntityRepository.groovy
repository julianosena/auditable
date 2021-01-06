package br.com.zup.itau.auditable.spring.boot.mongo.dbref

import org.springframework.data.repository.CrudRepository

interface MyDummyRefEntityRepository extends CrudRepository<MyDummyRefEntity, String> {
}