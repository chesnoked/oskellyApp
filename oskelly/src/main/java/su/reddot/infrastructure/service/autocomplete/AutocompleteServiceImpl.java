package su.reddot.infrastructure.service.autocomplete;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AutocompleteServiceImpl implements AutocompleteService {

	private Map<String, Autocompletable> autocompletables;

	@Autowired
	public AutocompleteServiceImpl(List<Autocompletable> autocompletableList) {
		this.autocompletables = autocompletableList.stream().collect(Collectors.toMap(autocompletable ->
						autocompletable.getAutocompleteType().getSimpleName().toLowerCase(),
				Function.identity()));
	}

	@Override
	public List<AutocompleteMatch> matchAutocompletes(String type, String value) {
		String cleanedType = type.replaceAll("[-_]]", "").toLowerCase();
		Autocompletable autocompletable = autocompletables.get(cleanedType);
		if (autocompletable != null) {
			String cleanedValue = value.trim();
			return autocompletable.matchAutocompletes(cleanedValue);
		} else {
			return Collections.emptyList();
		}
	}
}
