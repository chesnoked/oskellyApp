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
public class BankAccountsExporter implements FeedExporter{

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.feedExport.oneass.defaultPathToExportFile}")
    private String defaultExportPath;

    @Value("${app.feedExport.oneass.bank-accounts-filename}")
    private String exportFileName;

    @Override
    public String extractData() {
        String formatString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        return formatString + this.getActualBankAccountsFor1S();
    }

    @Override
    public void defaultDelivery() throws IOException{
        log.debug("Генерация выгрузки BankAccounts.XML для 1с");
        String exportData = this.extractData();

        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(defaultExportPath + exportFileName),
                        StandardCharsets.UTF_8));
        writer.write(exportData);
        writer.close();
        log.debug("Актуальная выгрузка BankAccounts.XML для 1с сохранена");
    }

    private String getActualBankAccountsFor1S() {
        return jdbcTemplate.queryForObject("SELECT xmlelement(NAME \"one-s-export\",\n" +
                "                  xmlelement(NAME \"meta-description\", 'BankAccounts'),\n" +
                "                  xmlelement(NAME \"meta-publistimestamp\", to_char(current_timestamp, 'DD.MM.YYYY  HH24:MI:SS')),\n" +
                "                  xmlelement(NAME BankAccounts,\n" +
                "                             (SELECT xmlagg(\n" +
                "                                 xmlelement(NAME BankAccount,\n" +
                "                                            xmlelement(NAME CustomerID, \"user\".id),\n" +
                "                                            xmlelement(NAME NumberAccount, \"user\".payment_account),\n" +
                "                                            xmlelement(NAME correspondentAccount, \"user\".correspondent_account),\n" +
                "                                            xmlelement(NAME BIC, \"user\".bik),\n" +
                "                                            xmlelement(NAME Currency, 'RUB')\n" +
                "                                 )\n" +
                "                             )\n" +
                "                              FROM \"user\")))", String.class);
    }
}
