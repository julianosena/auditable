package br.com.zup.itau.auditable.common.date;

import br.com.zup.itau.auditable.core.commit.CommitMetadata;
import java.time.ZonedDateTime;

/**
 * Date provider for {@link CommitMetadata#getCommitDate()}
 */
public interface DateProvider {
    ZonedDateTime now();
}
