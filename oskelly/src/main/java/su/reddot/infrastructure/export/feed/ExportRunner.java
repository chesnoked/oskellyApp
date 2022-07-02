package su.reddot.infrastructure.export.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import su.reddot.infrastructure.export.feed.oneass.*;

@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
@Slf4j
public class ExportRunner {

    private final YmlFeedExporter ymlFeedExporter;
    private final GmcFeedExporter gmcFeedExporter;
    private final FacebookFeedExporter facebookFeedExporter;

    private final SalesExporter oneAssSalesExporter;
    private final ProductsExporter oneAssProductsExporter;
    private final CustomersExporter oneAssCustomersExporter;
    private final BankAccountsExporter oneAssBankAccountsExporter;

    private final PaymentsExporter oneAssPaymentsExporter;

    @Scheduled(fixedRate = 1000*60*60)
    public void ymlExportTask(){

        try{
            ymlFeedExporter.defaultDelivery();
        }
        catch (Exception e) {
            log.error("Ошибка экспорта выгрузки YML", e);
        }

    }

    @Scheduled(fixedRate = 1000*60*60)
    public void gmcExportTask(){

        try{
            gmcFeedExporter.defaultDelivery();
        }
        catch (Exception e) {
            log.error("Ошибка экспорта выгрузки Google Merchant Center", e);
        }

    }

    @Scheduled(fixedRate = 1000*60*60)
    public void facebookExportTask(){

        try{
            facebookFeedExporter.defaultDelivery();
        }
        catch (Exception e) {
            log.error("Ошибка экспорта выгрузки в Facebook", e);
        }

    }

    @Scheduled(fixedRate = 1000*60*60*12)
    public void oneAssExportTask(){
        try{
            oneAssBankAccountsExporter.defaultDelivery();
        }catch (Exception e){
            log.error("Ошибка выгрузки файла 1с BankAccounts.xml: ", e);
        }

        try {
            oneAssCustomersExporter.defaultDelivery();
        }catch (Exception e){
            log.error("Ошибка выгрузки файла 1с Customers.xml: ", e);
        }

        try {
            oneAssSalesExporter.defaultDelivery();
        }catch (Exception e){
            log.error("Ошибка выгрузки файла 1с Sales.xml: ", e);
        }

        try {
            oneAssProductsExporter.defaultDelivery();
        }catch (Exception e){
            log.error("Ошибка выгрузки файла 1с Products.xml: ", e);
        }

        try {
            oneAssPaymentsExporter.defaultDelivery();
        }catch (Exception e){
            log.error("Ошибка выгрузки файла 1с PaymentsToSellers.xml: ", e);
        }
    }

}
