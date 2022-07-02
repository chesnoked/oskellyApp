package su.reddot.infrastructure.tilda;

public interface TildaImporter {

    /**
     * получить изображения, JS и СSS. это возможно только по проекту
     * @return true - если успех
     */
    void importProjectResources(Long projectId) throws Exception;

    void importOrUpdateFullPage(Long pageId) throws Exception;
}
