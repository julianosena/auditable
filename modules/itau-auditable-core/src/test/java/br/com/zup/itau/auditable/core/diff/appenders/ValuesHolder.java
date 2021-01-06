package br.com.zup.itau.auditable.core.diff.appenders;

/**
 * @author bartosz walacik
 */
class ValuesHolder {

    private ValueAlwaysEquals alwaysEquals = new ValueAlwaysEquals();
    private ValueNeverEquals neverEquals = new ValueNeverEquals();

    class ValueAlwaysEquals {
        public boolean equals(Object that){
            return true;
        }
    }

    class ValueNeverEquals{
        public boolean equals(Object that){
            return false;
        }
    }
}
