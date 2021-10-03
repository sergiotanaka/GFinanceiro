package org.pinguin.gf.gui.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * @param <T>
 */
public class TagBar<T> extends HBox {

	private static <T> StringConverter<T> defaultStringConverter() {
		return new StringConverter<T>() {
			@Override
			public String toString(T t) {
				return t == null ? null : t.toString();
			}

			@SuppressWarnings("unchecked")
			@Override
			public T fromString(String string) {
				return (T) string;
			}
		};
	}

	private final ObservableList<T> tags = FXCollections.observableArrayList();
	private final ObservableList<T> candidates = FXCollections.observableArrayList();
	private final TextField inputTextField;
	private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<StringConverter<T>>(this,
			"converter", defaultStringConverter());

	private boolean tagsContainsText(final String text) {
		return tags.stream().map(tag -> getConverter().toString(tag)).anyMatch(tagText -> tagText.equals(text));
	}

	/**
	 * Construtor.
	 */
	public TagBar() {
		// 1. Visual
		getStyleClass().setAll("tag-bar");
		getStylesheets().add(getClass().getResource("/META-INF/css/style.css").toExternalForm());

		inputTextField = new TextField();
		inputTextField.setOnAction(evt -> {
			final String text = inputTextField.getText();
			if (!text.isEmpty() && !tagsContainsText(text)) {
				tags.add(getConverter().fromString(text));
				inputTextField.clear();
			}
		});
		final Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>> provider = new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<String>>() {
			@Override
			public Collection<String> call(AutoCompletionBinding.ISuggestionRequest param) {
				return candidates.stream().map(t -> getConverter().toString(t))
						.filter(t -> t.toLowerCase().contains(param.getUserText().toLowerCase()))
						.collect(Collectors.toList());
			}
		};
		TextFields.bindAutoCompletion(inputTextField, provider);

		inputTextField.prefHeightProperty().bind(this.heightProperty());
		HBox.setHgrow(inputTextField, Priority.ALWAYS);
		inputTextField.setBackground(null);

		tags.addListener((ListChangeListener.Change<? extends T> change) -> {
			while (change.next()) {
				if (change.wasPermutated()) {
					final ArrayList<Node> newSublist = new ArrayList<>(change.getTo() - change.getFrom());
					for (int i = change.getFrom(), end = change.getTo(); i < end; i++) {
						newSublist.add(null);
					}
					for (int i = change.getFrom(), end = change.getTo(); i < end; i++) {
						newSublist.set(change.getPermutation(i), getChildren().get(i));
					}
					getChildren().subList(change.getFrom(), change.getTo()).clear();
					getChildren().addAll(change.getFrom(), newSublist);
				} else {
					if (change.wasRemoved()) {
						getChildren().subList(change.getFrom(), change.getFrom() + change.getRemovedSize()).clear();
					}
					if (change.wasAdded()) {
						getChildren().addAll(change.getFrom(),
								change.getAddedSubList().stream().map(Tag::new).collect(Collectors.toList()));
					}
				}
			}
		});
		getChildren().add(inputTextField);
	}

	public void setTags(final ObservableList<T> tags) {
		Bindings.bindContentBidirectional(this.tags, tags);
	}

	public void setCandidates(final ObservableList<T> candidates) {
		Bindings.bindContentBidirectional(this.candidates, candidates);
	}

	public ObjectProperty<StringConverter<T>> converterProperty() {
		return converter;
	}

	public final void setConverter(StringConverter<T> value) {
		converterProperty().set(value);
	}

	public final StringConverter<T> getConverter() {
		return converterProperty().get();
	}

	public ObservableList<T> getTags() {
		return tags;
	}

	public ObservableList<T> getCandidates() {
		return candidates;
	}

	private class Tag extends HBox {

		public Tag(final T tag) {
			getStyleClass().setAll("tag");
			final Button removeButton = new Button("X");
			removeButton.setOnAction((evt) -> tags.remove(tag));
			final Text text = new Text(getConverter().toString(tag));
			HBox.setMargin(text, new Insets(0, 0, 0, 5));
			getChildren().addAll(text, removeButton);
		}
	}
}
