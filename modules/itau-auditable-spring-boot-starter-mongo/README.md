## Javer Spring Boot MongoDB starter configuration

### Using Spring Boot MongoDB starter settings

Spring Boot automatically configures a `MongoClient` instance.
ItauAuditable starter uses this instance by default.

```yaml
spring:
  data:
    mongodb:
      database: my-mongo-database
```
Please refer to the spring-boot-starter-data-mongodb 
[reference documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-nosql.html#boot-features-mongodb) for details on how to configure MongoDB.

### Using ItauAuditable Spring Boot MongoDB starter settings

Sometimes it could be necessary to use a different MongoDB instance
for persisting ItauAuditable data.

To use a dedicated instance of MongoDB, configure ItauAuditable as shown below:

```yaml
itauAuditable:
  mongodb:
    host: localhost
    port: 27017
    database: itau-auditable-audit
    authentication-database: admin
    username: itauAuditable
    password: password
```

or:

```yaml
itauAuditable:
  mongodb:
    uri: mongodb://itauAuditable:password@localhost:27017/itau-auditable-audit&authSource=admin
```

Either `host` or `uri` has to set.

#### MongoClientSettings
If better control is required over how ItauAuditable configures the `MongoClient` instance,
you can configure a `MongoClientSettings` bean named `itauAuditableMongoClientSettings`.
If there is no such bean, default client options are used. 

For example, if you want to enable SSL and set socket timeout,
define this bean:

```java
@Bean("itauAuditableMongoClientSettings")
public MongoClientSettings clientSettings() {
    return MongoClientSettings.builder()
            .applyToSslSettings(builder -> builder.enabled(true))
            .applyToSocketSettings(
                builder -> builder.connectTimeout(500, TimeUnit.MILLISECONDS))
            .build();
}
```