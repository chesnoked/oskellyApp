package su.reddot.infrastructure.acquirer.impl.mdm.request;

public interface Request {
    String getSequenceToSign();

    void setSignature(String signrature);
}
