package br.com.zup.itau.auditable.repository.jql;

/**
* @author bartosz walacik
*/
public abstract class GlobalIdDTO {
    public abstract String value();

    @Override
    public String toString() {
        return "Dto("+value()+")";
    }
}
