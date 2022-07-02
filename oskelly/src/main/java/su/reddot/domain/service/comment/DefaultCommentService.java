package su.reddot.domain.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import su.reddot.domain.dao.CommentRepository;
import su.reddot.domain.dao.product.ProductRepository;
import su.reddot.domain.model.Comment;
import su.reddot.domain.model.notification.NewCommentNotification;
import su.reddot.domain.model.product.Product;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.sender.NotificationSender;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * @author Vitaliy Khludeev on 29.08.17.
 */
@Service
@RequiredArgsConstructor
public class DefaultCommentService implements CommentService {

	private final CommentRepository commentRepository;
	private final ProductRepository productRepository;

	private final UserService        userService;

	private final NotificationSender        sender;
	private final ApplicationEventPublisher pub;
	private final TemplateEngine            templateEngine;

	@Value("${resources.images.urlPrefix}")
	private String urlPrefix;
	@Value("${app.host}")
	private String host;

	@Override
	public List<CommentView> getComments(Long productId, TimeZone timezone) {

		return commentRepository.findAllByProductIdOrderByPublishTime(productId).stream()
				.map(c -> of(c, timezone)).collect(Collectors.toList());
	}

	@Override
	public CommentView publishComment(String text, Long userId, Long productId, TimeZone timezone) {

		User commentAuthor = userService.getUserById(userId).orElseThrow(
				() -> new IllegalArgumentException("User with id: " + userId + " not found"));

		Product product = productRepository.findOne(productId);
		if(product == null) {
			throw new IllegalArgumentException("Product with id: " + productId + " not found");
		}

		Comment comment = new Comment()
				.setProduct(product)
				.setPublisher(commentAuthor)
				.setText(text)
				.setPublishTime(ZonedDateTime.now());

		commentRepository.save(comment);

		User seller = product.getSeller();
		/* Продавец не должен получать извещения
		о своем собственном комментарии к своему же товару. */
		if (!commentAuthor.equals(seller)) {
			pub.publishEvent(new NewCommentNotification()
					.setComment(comment)
					.setUser(seller));

			send(comment);
		}


		return of(comment, timezone);
	}

	private CommentView of(Comment c, TimeZone timezone) {
		return new CommentView(c.getId(), c.getText(),
				DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(c.getPublishTime().withZoneSameInstant(timezone.toZoneId())),
				c.getPublishTime(),
				c.getPublisher().getId(),
				c.getPublisher().getNickname(),
				c.getPublisher().getAvatarPath() != null ? urlPrefix + c.getPublisher().getAvatarPath() : null);

	}

	private void send(Comment comment) {

		Context ctx = new Context();
		ctx.setVariable("comment", comment);
		ctx.setVariable("host", host);

		String text = templateEngine.process("mail/new-comment", ctx);

		String sellerEmail = comment.getProduct()
				.getSeller()
				.getEmail();

		sender.send(sellerEmail, "Новый комментарий к вашему товару", text);

	}
}
