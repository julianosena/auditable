package br.com.zup.itau.auditable.common.date;

import java.time.ZonedDateTime;

public class DefaultDateProvider implements DateProvider {

    @Override
    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}
