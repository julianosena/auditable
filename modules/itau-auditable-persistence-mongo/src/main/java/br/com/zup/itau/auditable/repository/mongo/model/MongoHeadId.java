package br.com.zup.itau.auditable.repository.mongo.model;

import org.bson.Document;
import br.com.zup.itau.auditable.core.commit.CommitId;

/**
 * @author pawel szymczyk
 */
public class MongoHeadId {
    public static final String COLLECTION_NAME = "audit_head_id";
    private static final String KEY = "id";

    private final String id;

    public MongoHeadId(Document doc) {
        this.id = doc.getString(KEY);
    }

    public MongoHeadId(CommitId id) {
        this.id = id.value();
    }

    public CommitId toCommitId() {
        return CommitId.valueOf(getId());
    }

    public Document toDocument() {
        return new Document(KEY, id);
    }

    public Document getUpdateCommand(){
        return new Document("$set", toDocument());
    }

    private String getId() {
        return id;
    }
}
