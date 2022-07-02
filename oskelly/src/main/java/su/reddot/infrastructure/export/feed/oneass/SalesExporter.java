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
public class SalesExporter implements FeedExporter{

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.feedExport.oneass.defaultPathToExportFile}")
    private String defaultExportPath;

    @Value("${app.feedExport.oneass.sales-filename}")
    private String exportFileName;

    @Override
    public String extractData() {
        String formatString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        return formatString + this.getActualSalesFor1S();
    }

    @Override
    public void defaultDelivery() throws IOException {
        log.debug("Генерация выгрузки Sales.XML для 1с");
        String exportData = this.extractData();

        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(defaultExportPath + exportFileName),
                        StandardCharsets.UTF_8));
        writer.write(exportData);
        writer.close();
        log.debug("Актуальная выгрузка Sales.XML для 1с сохранена");
    }

    private String getActualSalesFor1S() {
        return jdbcTemplate.queryForObject("SELECT xmlelement(NAME \"one-s-export\",\n" +
                "                  xmlelement(NAME \"meta-description\", 'Sales'),\n" +
                "                  xmlelement(NAME \"meta-publistimestamp\", to_char(current_timestamp, 'DD.MM.YYYY  HH24:MI:SS')),\n" +
                "                  xmlelement(NAME Sales,\n" +
                "                             (SELECT xmlagg(\n" +
                "                                 xmlelement(NAME Sale,\n" +
                "                                            xmlelement(NAME ID, o.id),\n" +
                "                                            --- Номер ЗАКАЗА в системе\n" +
                "                                            xmlelement(NAME orderState, o.state),\n" +
                "                                            --- Сосотяние, типа \"оплата производится\", \"проблемы с картой\", и т. д.\n" +
                "                                            xmlelement(NAME orderStateDate,\n" +
                "                                                       to_char(o.state_time, 'DD.MM.YYYY  HH24:MI:SS')),\n" +
                "                                            --- тут нужно пояснить, что это ДАТА-ВРЕМЯ последнего обновления заказа, может не совпадать с датой оплаты\n" +
                "                                            xmlelement(NAME BuyerId, o.buyer_id),\n" +
                "                                            xmlelement(NAME orderAmount, ROUND(o.amount, 2)),\n" +
                "                                            --- Сумма по заказу, с учетом скидок - столько списывается по чеку\n" +
                "                                            xmlelement(NAME promoCode, o.promo_code_id),\n" +
                "                                            --- ID примененного промокода, если есть - см. справочник промокодов\n" +
                "                                            (SELECT XMLAGG(xmlelement(NAME certificate,\n" +
                "                                                                      xmlelement(NAME ID, gift_card.id),\n" +
                "                                                                      --- ID подарочного сертификата\n" +
                "                                                                      xmlelement(NAME codeValue, gift_card.code),\n" +
                "                                                                      --- Код подарочного сертификата\n" +
                "                                                                      xmlelement(NAME certificateAmount,\n" +
                "                                                                                 ROUND(gift_card.amount, 2)))\n" +
                "                                                           --- Значение подарочного сертификата\n" +
                "                                            )\n" +
                "                                             FROM gift_card\n" +
                "                                             WHERE gift_card.order_id = o.id\n" +
                "                                            ),\n" +
                "                                            xmlelement(NAME BuyerCheck, o.buyer_check),\n" +
                "                                            --- это чек от онлайн-кассы, можно парсить его на нужные параметры\n" +
                "                                            --- ВАЖНО, не учтено, что обща стоимость заказа может быть меньше стоимости всехвещей в заказе\n" +
                "                                            --- за счет: Скидки(промокода), предоплаченного сертификата, баллов(которые можно потратить на покупку.\n" +
                "                                            --- Не отмечена общая стоимость заказа\n" +
                "                                            xmlelement(NAME orderPositions,\n" +
                "                                                       (SELECT xmlagg(\n" +
                "                                                           xmlelement(NAME orderPosition,\n" +
                "                                                                      xmlelement(NAME orderPositionId, op.id),\n" +
                "                                                                      -- идетификатор строки заказа в Системе\n" +
                "                                                                      xmlelement(NAME productItemId,\n" +
                "                                                                                 op.product_item_id),\n" +
                "                                                                      --- ВАЖНО, это ссыка на на Product, а на ProductItem\n" +
                "                                                                      xmlelement(NAME sellerId, pr.seller_id),\n" +
                "                                                                      --- ID Продавца вещи\n" +
                "                                                                      xmlelement(NAME publishTimeStamp,\n" +
                "                                                                                 to_char(publish_time,\n" +
                "                                                                                         'DD.MM.YYYY  HH24:MI:SS')),\n" +
                "                                                                      --- дата и время публиации (имеется в виду, что модератор пропустил вещь)\n" +
                "                                                                      xmlelement(NAME acceptancePrice, ROUND(\n" +
                "                                                                          op.amount *(1 - op.commission), 2)),\n" +
                "                                                                      --- \"Цена поступления товара\"\n" +
                "                                                                      xmlelement(NAME comission,\n" +
                "                                                                                 ROUND(op.amount * op.commission, 2)),\n" +
                "                                                                      --- комиссия сервиса(cумма в рублях)\n" +
                "                                                                      xmlelement(NAME amount, op.amount),\n" +
                "                                                                      --- стоимость вещи в заказе(с учетом комиссии сервиса)\n" +
                "                                                                      xmlelement(NAME deliveryCost, op.delivery_cost),\n" +
                "                                                                      --- стоимость доставки\n" +
                "                                                                      xmlelement(NAME saleDate, to_char((SELECT st.at\n" +
                "                                                                                                         FROM\n" +
                "                                                                                                           state_change st\n" +
                "                                                                                                         WHERE\n" +
                "                                                                                                           st.product_item_id\n" +
                "                                                                                                           = pri.id AND\n" +
                "                                                                                                           st.new_state\n" +
                "                                                                                                           =\n" +
                "                                                                                                           'SALE_CONFIRMED'\n" +
                "                                                                                                         LIMIT 1),\n" +
                "                                                                                                        'DD.MM.YYYY  HH24:MI:SS')),\n" +
                "                                                                      --- Дата продажи\n" +
                "                                                                      xmlelement(NAME paymentDate, CONCAT(\n" +
                "                                                                          (o.buyer_check :: JSON -> 'Date' -> 'Date'\n" +
                "                                                                           -> 'Day'), '.',\n" +
                "                                                                          (o.buyer_check :: JSON -> 'Date' -> 'Date'\n" +
                "                                                                           -> 'Month'), '.', '20',\n" +
                "                                                                          (o.buyer_check :: JSON -> 'Date' -> 'Date'\n" +
                "                                                                           -> 'Year'), ' ',\n" +
                "                                                                          (o.buyer_check :: JSON -> 'Date' -> 'Time'\n" +
                "                                                                           -> 'Hour'), ':',\n" +
                "                                                                          (o.buyer_check :: JSON -> 'Date' -> 'Time'\n" +
                "                                                                           -> 'Minute'), ':',\n" +
                "                                                                          (o.buyer_check :: JSON -> 'Date' -> 'Time'\n" +
                "                                                                           -> 'Second')))\n" +
                "                                                           )\n" +
                "                                                       )\n" +
                "                                                        FROM order_position op\n" +
                "                                                          JOIN product_item pri ON op.product_item_id = pri.id\n" +
                "                                                          JOIN product pr ON pr.id = pri.product_id\n" +
                "                                                        WHERE op.order_id = o.id AND op.is_effective = TRUE\n" +
                "                                                       )\n" +
                "                                            )\n" +
                "                                 )\n" +
                "                             )\n" +
                "                              FROM \"order\" o\n" +
                "                              WHERE o.state IN ('HOLD', 'HOLD_COMPLETED')\n" +
                "                               --- в выборку попадают только заказы, по которым мы захолдили или списали деньги\n" +
                "                             )\n" +
                "                  ))", String.class);
    }

}
