package su.reddot.domain.model.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import su.reddot.domain.model.Authority;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class UserAuthorityBinding {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private User user;

	@ManyToOne
	private Authority authority;

	public UserAuthorityBinding(User user, Authority authority) {
		this.user = user;
		this.authority = authority;
	}
}
