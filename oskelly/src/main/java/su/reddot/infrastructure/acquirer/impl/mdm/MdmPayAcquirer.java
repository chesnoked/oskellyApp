package su.reddot.infrastructure.acquirer.impl.mdm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import su.reddot.infrastructure.acquirer.Acquirer;
import su.reddot.infrastructure.acquirer.Payable;
import su.reddot.infrastructure.acquirer.TransactionHandler;
import su.reddot.infrastructure.acquirer.impl.mdm.request.Request;
import su.reddot.infrastructure.acquirer.impl.mdm.request.impl.FinancialRequest;
import su.reddot.infrastructure.acquirer.impl.mdm.request.impl.HoldCompletionRequest;
import su.reddot.infrastructure.acquirer.impl.mdm.request.impl.RefundRequest;
import su.reddot.infrastructure.acquirer.impl.mdm.type.TransactionInfo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MdmPayAcquirer implements Acquirer {

    private final MdmPayConfiguration prop;
    private final ObjectMapper mapper;

    private final List<TransactionHandler> registeredTransactionHandlers = new ArrayList<>();
    @Override
    public String getPaymentRequest(Payable p, String nullableReturnUrl, ZonedDateTime requestDateTime) {

        String cookedReturnUrl = nullableReturnUrl == null? prop.getReturnUrl()
            : String.format("%s%s", prop.getReturnUrl(), nullableReturnUrl);

        try {
            Request rawRequest = new FinancialRequest(
                p.getPaymentAmount(),    p.getOrderId(),   requestDateTime,
                prop.getMerchantName(),  prop.getToken(),
                prop.getCallbackUrl(),   cookedReturnUrl);

            rawRequest.setSignature(
                    computeSignature(rawRequest.getSequenceToSign())
            );

            return encoded(mapper.writeValueAsString(rawRequest));

        } catch (Exception e) {
            log.error("Оплачиваемый заказ: {}, сумма: {}", p.getOrderId(), p.getPaymentAmount().toPlainString(), e.getMessage(), e);

            /* Клиент не может исправить ошибку формирования запроса, так как она
            * возникает по причине неправильной работы приложения, а не из-за неверных
            * действий клиента.*/
            throw new RuntimeException();
        }
    }

    @Override
    public void handleTransactionInfo(String i) {
        try {
            log.info("Сообщение от банка: {}", i);
            TransactionInfo transactionInfo = mapper.readValue(i, TransactionInfo.class);

            String actualSignature = transactionInfo.getSignature();
            String expectedSignature = computeSignature(transactionInfo.getSequenceToSign());
            if (!actualSignature.equalsIgnoreCase(expectedSignature)) {
                log.error("Сообщение имеет некорректную подпись. Ожидаемое значение: {}, действительное значение: {}",
                        expectedSignature, actualSignature);
                return;
            }

            registeredTransactionHandlers.forEach(t -> t.handleTransaction(transactionInfo));

        } catch (Exception e) {
            log.error("Ошибка обработки сообщения от банка. Данные транзакции: {}",
                    i, e);
        }
    }

    @Override
    public void registerTransactionHandler(TransactionHandler h) {
        registeredTransactionHandlers.add(h);
    }

    @Override
    public void completeHold(int transactionId, Long orderId, BigDecimal holdAmount, ZonedDateTime requestDateTime) {

        Request rawRequest = new HoldCompletionRequest(
                prop.getToken(), transactionId, orderId, requestDateTime,
                holdAmount);

        try {
            rawRequest.setSignature(
                    computeSignature(rawRequest.getSequenceToSign())
            );
            RestTemplate client = new RestTemplate();
            TransactionInfo holdCompletionResult = client.postForObject(prop.getApiEndpoint() + "/hold_completion",
                    rawRequest,
                    TransactionInfo.class);

            // TODO

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void refund(int transactionId, Long orderId, BigDecimal refundAmount, ZonedDateTime requestDateTime) {
        Request rawRequest = new RefundRequest(prop.getToken(), transactionId, orderId, requestDateTime, refundAmount);

        try {
            rawRequest.setSignature(
                    computeSignature(rawRequest.getSequenceToSign())
            );
            RestTemplate client = new RestTemplate();
            TransactionInfo refundResult = client.postForObject(prop.getApiEndpoint() + "/refund", rawRequest, TransactionInfo.class);

            // TODO

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String computeSignature(String sequenceToSign)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac signBox = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                prop.getSigningKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        signBox.init(secretKeySpec);
        byte[] signedSequence = signBox.doFinal(sequenceToSign.getBytes(StandardCharsets.UTF_8));

        return String.valueOf(Hex.encode(signedSequence));
    }

    private String encoded(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
}
