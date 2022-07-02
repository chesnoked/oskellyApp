package su.reddot.infrastructure.service.autocomplete;

import java.util.List;

public interface AutocompleteService {

	List<AutocompleteMatch> matchAutocompletes(String type, String value);
}
