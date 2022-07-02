package su.reddot.infrastructure.acquirer.impl.mdm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.junit.BeforeClass;
import org.junit.Test;
import su.reddot.infrastructure.acquirer.Acquirer;
import su.reddot.infrastructure.acquirer.Payable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MdmPayAcquirerTest {

    private static Acquirer acquirer;

    @BeforeClass
    public static void setUp() throws JsonProcessingException {

        /* рассмотреть возможность подключение контекста приложения */
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        MdmPayConfiguration configuration = new MdmPayConfiguration();
        configuration.setToken("BA:07:D3:CB:BC:37:82:4C:97:06:93:F3:A5:64:DF:F8");
        configuration.setSigningKey("C7B901B4FD0146CC8F6DE9FF3DCDDD5C");
        configuration.setMerchantName("OSKELLY.RU");
        configuration.setCallbackUrl("callback_url");
        configuration.setReturnUrl("return_url");

        acquirer = new MdmPayAcquirer(configuration, mapper);
    }

    @Test
    public void shouldBuildCorrectPaymentRequest() {

        Payable order = new Payable() {
            @Override
            public Long getOrderId() {
                return 42L;
            }

            @Override
            public BigDecimal getPaymentAmount() {
                return new BigDecimal(123.99);
            }
        };

        String expectedPaymentRequest =
                "eyJ0b2tlbiI6IkJBOjA3OkQzOkNCOkJDOjM3OjgyOjRDOjk3OjA2OjkzOkYzOkE1OjY0OkRGOkY4IiwiY2FsbGJhY2tfdXJsIjoi"
                + "Y2FsbGJhY2tfdXJsIiwicmV0dXJuX3VybCI6InJldHVybl91cmwiLCJtZXJjaGFudF9uYW1lIjoiT1NLRUxMWS5SVSIsIm9yZGVyX"
                + "2lkIjo0Miwic2lnbmF0dXJlIjoiODhjNzcwOGVhMjBkM2E5NDY5NWIwMGIyODM2OWQxZmYxZjQ1YjEzYmU1ODgzNjJjODM5MDFmND"
                + "UzNWNjMWQ4MyIsImFtb3VudCI6eyJ2YWx1ZSI6MTIzLjk5LCJjdXJyZW5jeSI6IlJVQiJ9LCJyZXF1ZXN0X2RhdGUiOiIyMDAwLTA"
                + "xLTAxVDAwOjAwOjAwKzAzOjAwIiwic2VxdWVuY2VfdG9fc2lnbiI6InRva2VuPUJBOjA3OkQzOkNCOkJDOjM3OjgyOjRDOjk3OjA2"
                + "OjkzOkYzOkE1OjY0OkRGOkY4b3JkZXJfaWQ9NDJyZXF1ZXN0X2RhdGU9MjAwMC0wMS0wMVQwMDowMDowMCswMzowMGFtb3VudC52Y"
                + "Wx1ZT0xMjMuOTlhbW91bnQuY3VycmVuY3k9UlVCIn0=";

        ZonedDateTime paymentRequestDate = LocalDateTime.of(2000, Month.JANUARY, 1, 0, 0, 0)
                .atZone(ZoneId.of("UTC+3"));

        String paymentRequest = acquirer.getPaymentRequest(order, paymentRequestDate);
        assertThat(paymentRequest, is(equalTo(expectedPaymentRequest)));
    }

}
