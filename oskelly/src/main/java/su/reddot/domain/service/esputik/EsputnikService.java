package su.reddot.domain.service.esputik;

import com.fasterxml.jackson.core.JsonProcessingException;
import su.reddot.domain.model.esputink.event.EmailSubscribedEvent;

public interface EsputnikService {
    public void subscribe(EmailSubscribedEvent event) throws JsonProcessingException;
}
