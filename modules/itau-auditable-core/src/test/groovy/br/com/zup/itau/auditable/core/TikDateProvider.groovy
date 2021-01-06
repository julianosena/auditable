package br.com.zup.itau.auditable.core

import br.com.zup.itau.auditable.common.date.DateProvider

import java.time.ZonedDateTime

class TikDateProvider implements DateProvider {
    private ZonedDateTime dateTime = ZonedDateTime.now()

    void set(ZonedDateTime now) {
        dateTime = now
    }

    @Override
    synchronized ZonedDateTime now() {
        def now = dateTime
        dateTime = dateTime.plusSeconds(1)
        now
    }
}
