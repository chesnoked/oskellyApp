package su.reddot.domain.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import su.reddot.domain.model.enums.AuthorityName;

import javax.persistence.*;

@Data
@Entity
public class Authority implements GrantedAuthority {

    public enum AuthorityType{
        ADMIN, MODERATOR
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private AuthorityName name;

    @Enumerated(value = EnumType.STRING)
    private AuthorityType type;

    @Override
    public String getAuthority() {
        return name.name();
    }


}
