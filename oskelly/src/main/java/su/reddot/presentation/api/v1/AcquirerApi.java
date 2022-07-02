package su.reddot.presentation.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.reddot.infrastructure.acquirer.Acquirer;

@RestController
@RequestMapping("/api/v1/acquirers")
@RequiredArgsConstructor
public class AcquirerApi {

    private final Acquirer acquirer;

    @PostMapping("/mdm/callback")
    ResponseEntity<?> handleTransactionInfo(@RequestBody String transactionInfo) {

        /* FIXME Сразу отдавать 200, обработчики транзакции запускать параллельно */
        acquirer.handleTransactionInfo(transactionInfo);

        return ResponseEntity.ok().build();
    }
}
