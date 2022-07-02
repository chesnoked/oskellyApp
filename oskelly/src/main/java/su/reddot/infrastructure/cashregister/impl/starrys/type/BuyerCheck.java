package su.reddot.infrastructure.cashregister.impl.starrys.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/** Результат выполнения команды Complex (команды получения чека)
 *
 * @apiNote
 * <ul>
 *     <li>поля, которые в документации имеют тип 32х разрядного целого,
 * десериализуются в тип long: апи передает беззнаковые значения, которые
 * могут лежать вне диапазона значений типа int.</li>
 *     <li>содержит только необходимые для приложения поля
 *     из всех перечисленных в документации</li>
 * </ul>
 * */
@JsonNaming(CustomNamingStrategy.class)
@Setter(AccessLevel.PACKAGE)
public class BuyerCheck {

    private Response response;

    private String deviceRegistrationNumber;

    @JsonProperty("FNSerialNumber")
    private String fnSerialNumber;
    private long fiscalDocNumber;
    private long fiscalSign;

    private int docNumber;
    private byte documentType;

    private long grandTotal;

    /** QR-код чека */
    @JsonProperty("QR")
    private String qr;

    /** Complex - фасадная команда, которая скрывает за собой
     * вызов набора команд, выполение которых необходимо для получения одного чека.
     * Результаты выполнения каждой такой команды. */
    private List<SingleCommandResponse> responses;

    /** Дата и время создания чека в форматированном виде. */
    public Optional<String> getFormattedDateTime() {
        return Optional.ofNullable(
                getDateTime().map(dt -> dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .orElse(null));
    }

    /** Дата и время в utc зоне (+0) */
    private Optional<LocalDateTime> getDateTime() {

        Optional<Tag> dateTimeTagIfAny = findTagInWholeResponse(1012);
        if (!dateTimeTagIfAny.isPresent()) { return Optional.empty(); }

        UnixTimeTag dateTimeTag = (UnixTimeTag) dateTimeTagIfAny.get();
        return Optional.of(dateTimeTag.getValue());
    }


    /** Тип документа (как правило "приход") в человекопонятной форме. */
    public Optional<String> getDocumentType() {

        String humanReadableForm = null;
        switch (documentType) {
            case 0:
                humanReadableForm = "Приход";
                break;
            case 1:
                humanReadableForm = "Расход";
                break;
            case 2:
                humanReadableForm = "Возврат прихода";
                break;
            case 3:
                humanReadableForm = "Возврат расхода";
        }

        return Optional.ofNullable(humanReadableForm);
    }

    public int getDocumentNumber() { return docNumber; }

    public Optional<Long> getTurnNumber() {

        Optional<Tag> turnNumberIfAny = findTagInWholeResponse(1038L);
        if (!turnNumberIfAny.isPresent()) { return Optional.empty(); }

        Uint32Tag turnNumber = (Uint32Tag) turnNumberIfAny.get();
        return Optional.of(turnNumber.getValue());
    }

    /** Общая сумма чека. Дробная часть выводиться только если она есть. */
    public Optional<String> getFormattedTotal() {

        DecimalFormat        f = new DecimalFormat("###,###,###.#####");
        DecimalFormatSymbols s = new DecimalFormatSymbols();

        s.setGroupingSeparator(' ');
        s.setDecimalSeparator(',');
        f.setDecimalFormatSymbols(s);
        f.setMinimumFractionDigits(2); // иначе 42,10 выводится как 42,1

        String formatted = f.format(new BigDecimal(grandTotal).movePointLeft(2));
        if (formatted.endsWith(",00")) {
            formatted = formatted.substring(0, formatted.lastIndexOf(",00"));
        }

        return Optional.of(formatted);
    }

    /** Безналичная часть от общей суммы чека с обязательной дробной частью. */
    public Optional<String> getFormattedNonCash() {

        Optional<Tag> nonCashIfAny = findTagInWholeResponse(1081L);
        if (!nonCashIfAny.isPresent()) { return Optional.empty(); }

        MoneyTag nonCash = (MoneyTag) nonCashIfAny.get();

        DecimalFormat        f = new DecimalFormat("###,###,###.#####");
        DecimalFormatSymbols s = new DecimalFormatSymbols();

        s.setGroupingSeparator(' ');
        s.setDecimalSeparator(',');
        f.setDecimalFormatSymbols(s);
        f.setMinimumFractionDigits(2);

        return Optional.of(
                f.format(new BigDecimal(nonCash.getValue()).movePointLeft(2)));
    }

    public Optional<ShopInfo> getShopInfo() {

        Tag nullableName = findTagInWholeResponse(1048L).orElse(null);
        Tag nullableAddress = findTagInWholeResponse(1009L).orElse(null);
        Tag nullableLocation = findTagInWholeResponse(1187L).orElse(null);
        Tag nullableTin = findTagInWholeResponse(1018L).orElse(null);

        if (nullableName == null
                || nullableAddress == null
                || nullableLocation == null
                || nullableTin == null) { return Optional.empty(); }


        StringTag name = (StringTag) nullableName;
        StringTag address = (StringTag) nullableAddress;
        StringTag location = (StringTag) nullableLocation;
        StringTag tin = (StringTag) nullableTin;

        ShopInfo shopInfo = new ShopInfo()
                .setName(name.getValue())
                .setAddress(address.getValue())
                .setLocation(location.getValue())
                .setTin(tin.getValue());


        return Optional.of(shopInfo);
    }

    public FiscalDetails getFiscalDetails() {

        return new FiscalDetails()
                .setDeviceRegistrationNumber(deviceRegistrationNumber)
                .setDocumentNumber(fiscalDocNumber)
                .setSerialNumber(fnSerialNumber)
                .setSign(fiscalSign);
    }

    public Optional<String> getQrCode() { return Optional.ofNullable(qr); }

    public boolean hasErrors() { return response.getError() != 0; }

    /** Найти тэг с данным идентификатором во всем ответе */
    private Optional<Tag> findTagInWholeResponse(long id) {

        Optional<SingleCommandResponse> closeDocumentResponseIfAny = responses.stream()
                .filter(r -> "/fr/api/v2/CloseDocument".equals(r.getPath()))
                .findFirst();
        if (!closeDocumentResponseIfAny.isPresent()) { return Optional.empty(); }

        return findFirst(id, closeDocumentResponseIfAny.get().getResponse().getFiscalDocument());
    }

    private Optional<Tag> findFirst(long id, Tag in) {
        if (!(in instanceof NodeTag)) {
            return Optional.ofNullable(in.getId() == id? in : null);
        }

        NodeTag nodeTag = (NodeTag) in;
        for (Tag tag : nodeTag.getValue()) {
            Optional<Tag> found = findFirst(id, tag);

            if (found.isPresent()) { return found; }
        }

        return Optional.empty();
    }

    /** Данные магазина. */
    @Getter @Setter @Accessors(chain = true)
    public static class ShopInfo {

        private String name;

        /** Адрес расчета */
        private String address;

        /** Место расчета */
        private String location;

        /** ИНН юридического лица (taxpayer identification number) (не путать с ИНН физ.лица) */
        private String tin;
    }

    /** Данные фискального документа. */
    @Getter @Setter @Accessors(chain = true)
    public static class FiscalDetails {

        /** Регистрационный номер кассового терминала (РН ККТ). */
        private String deviceRegistrationNumber;

        /** Номер фискального накопителя, в котором сформирован документ (ФН). */
        private String serialNumber;

        /** Номер фискального докумета (ФД). */
        private long documentNumber;

        /** Фискальный признак документа (ФП). */
        private long sign;
    }
}
