package su.reddot.domain.service.pro;

import su.reddot.domain.model.user.User;

/**
 * @author Vitaliy Khludeev on 31.07.17.
 */
public interface ProService {

	SellerInfoResponse getSellerInfo(User user, Integer page);
}
