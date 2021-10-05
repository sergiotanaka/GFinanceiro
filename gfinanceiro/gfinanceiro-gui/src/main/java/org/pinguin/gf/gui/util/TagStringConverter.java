package org.pinguin.gf.gui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.pinguin.gf.service.api.journalentry.TagTO;

import javafx.util.StringConverter;

/**
 * Conversor para String do TagTO.
 */
public class TagStringConverter extends StringConverter<TagTO> {

	private final List<TagTO> candidates = new ArrayList<>();

	public TagStringConverter(final Collection<TagTO> candidates) {
		this.candidates.addAll(candidates);
	}

	@Override
	public String toString(TagTO object) {
		return object.getName();
	}

	@Override
	public TagTO fromString(String string) {
		final Optional<TagTO> found = candidates.stream().filter(t -> t.getName().equals(string)).findFirst();
		if (!found.isEmpty()) {
			return found.get();
		}
		return new TagTO(string);
	}

	public static TagStringConverter instance(final Collection<TagTO> candidates) {
		return new TagStringConverter(candidates);
	}

}
