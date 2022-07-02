package su.reddot.infrastructure.export.feed.oneass;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import su.reddot.infrastructure.export.feed.FeedExporter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentsExporter implements FeedExporter{

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.feedExport.oneass.defaultPathToExportFile}")
    private String defaultExportPath;

    @Value("${app.feedExport.oneass.payments-filename}")
    private String exportFileName;

    @Override
    public String extractData() {
        String formatString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        return formatString + this.getActualPaymentsFeedFor1S();
    }

    @Override
    public void defaultDelivery() throws IOException{
        log.debug("Генерация реестра платежек PaymentsToSellers.xml для 1с");
        String exportData = this.extractData();

        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(defaultExportPath + exportFileName),
                        StandardCharsets.UTF_8));
        writer.write(exportData);
        writer.close();
        log.debug("Актуальный реестр PaymentsToSellers.xml для 1с сохранена");
    }

    private String getActualPaymentsFeedFor1S() {
        return jdbcTemplate.queryForObject("SELECT xmlelement(NAME \"one-s-export\",\n" +
                "                  xmlelement(NAME \"meta-description\", 'PaymentsToSellers'),\n" +
                "                  xmlelement(NAME \"meta-publistimestamp\", to_char(current_timestamp, 'DD.MM.YYYY  HH24:MI:SS')),\n" +
                "                  xmlelement(NAME paymentdocuments,\n" +
                "                             (SELECT xmlagg(\n" +
                "                                 xmlelement(NAME \"document\",\n" +
                "                                            xmlelement(NAME orderPositionId, op.id),\n" +
                "                                            -- идетификатор строки заказа в Системе\n" +
                "                                            xmlelement(NAME productItemId, op.product_item_id),\n" +
                "                                            xmlelement(NAME sellerId, pr.seller_id),\n" +
                "                                            --- ID Продавца вещи\n" +
                "                                            xmlelement(NAME BankAccount,\n" +
                "                                                       xmlelement(NAME CustomerID, usr.id),\n" +
                "                                                       xmlelement(NAME NumberAccount, usr.payment_account),\n" +
                "                                                       xmlelement(NAME correspondentAccount,\n" +
                "                                                                  usr.correspondent_account),\n" +
                "                                                       xmlelement(NAME BIC, usr.bik),\n" +
                "                                                       xmlelement(NAME Currency, 'RUB')\n" +
                "                                            ),\n" +
                "                                            xmlelement(NAME comission, op.commission),\n" +
                "                                            --- комиссия сервиса\n" +
                "                                            xmlelement(NAME amount, ROUND(op.amount, 2)),\n" +
                "                                            --- стоимость вещи в заказе(с учетом комиссии сервиса)\n" +
                "                                            xmlelement(NAME deliveryCost, op.delivery_cost),\n" +
                "                                            xmlelement(NAME paymetToSellerAmount,\n" +
                "                                                       ROUND(op.amount * (1 - op.commission)), 2),\n" +
                "                                            xmlelement(NAME \"recomended_date\",\n" +
                "                                                       (SELECT to_char(MAX(sc.at) + INTERVAL '7 days', 'DD.MM.YYYY')\n" +
                "                                                        FROM state_change sc\n" +
                "                                                        WHERE sc.product_item_id = pri.id AND\n" +
                                                                            //FIXME перечень статусов полный
                "                                                              sc.new_state = 'SHIPPED_TO_CLIENT'))\n" +
                "                                 ))\n" +
                "                              FROM order_position op\n" +
                "                                JOIN product_item pri ON op.product_item_id = pri.id\n" +
                "                                JOIN product pr ON pr.id = pri.product_id\n" +
                "                                JOIN \"user\" usr ON usr.id = pr.seller_id\n" +
                "\n" +
                "                              WHERE pri.state IN ('SHIPPED_TO_CLIENT')\n" +
                "                             )\n" +
                "                  )\n" +
                ")\n", String.class);

    }
}
