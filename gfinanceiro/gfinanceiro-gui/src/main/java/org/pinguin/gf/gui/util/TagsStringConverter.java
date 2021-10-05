package org.pinguin.gf.gui.util;

import java.util.Collection;
import java.util.stream.Collectors;

import org.pinguin.gf.service.api.journalentry.TagTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;

public class TagsStringConverter extends StringConverter<ObservableList<TagTO>> {

	private final TagStringConverter tagStrConv;

	public TagsStringConverter(final Collection<TagTO> candidates) {
		this.tagStrConv = new TagStringConverter(candidates);
	}

	@Override
	public String toString(final ObservableList<TagTO> object) {
		return object.stream().map(t -> tagStrConv.toString(t)).collect(Collectors.joining(","));
	}

	@Override
	public ObservableList<TagTO> fromString(final String string) {
		final ObservableList<TagTO> list = FXCollections.observableArrayList();
		for (final String tag : string.split(",")) {
			list.add(tagStrConv.fromString(tag));
		}
		return list;
	}

	public static TagsStringConverter instance(final Collection<TagTO> candidates) {
		return new TagsStringConverter(candidates);
	}

}
