package su.reddot.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import su.reddot.infrastructure.service.autocomplete.AutocompleteMatch;
import su.reddot.infrastructure.service.autocomplete.AutocompleteService;

import java.util.List;

@RestController
public class AutocompleteController {

	private final AutocompleteService autocompleteService;

	@Autowired
	public AutocompleteController(AutocompleteService autocompleteService) {
		this.autocompleteService = autocompleteService;
	}

	@RequestMapping("/autocomplete/{type}")
	public ResponseEntity<?> autocomplete(@PathVariable("type") String type, @RequestParam("value") String value) {
		List<AutocompleteMatch> matches = autocompleteService.matchAutocompletes(type, value);
		return ResponseEntity.ok(matches);
	}
}
