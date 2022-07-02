package su.reddot.infrastructure.service.autocomplete;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AutocompleteMatch {
	private Long id;
	private String text;
}
