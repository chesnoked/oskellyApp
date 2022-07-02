package su.reddot.infrastructure.cashregister;

public interface CashRegister {

    String requestCheck(Checkable c);

    String getQrUrl(String qrCode);
}
