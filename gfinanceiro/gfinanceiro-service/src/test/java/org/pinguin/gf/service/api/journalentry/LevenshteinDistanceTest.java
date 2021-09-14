package org.pinguin.gf.service.api.journalentry;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LevenshteinDistanceTest {

	@Test
	public void testLevenshteinDistance() {
		System.out.println("test");

		float dist = levenshteinDistance("Veja neste post como mostrar como utilizar o Lombok",
				"Veja neste post como mostrar como utilizar o JUnit");

		Assertions.assertTrue(dist < 0.2f, () -> "Dist: " + dist + " >= 0.2f");
	}

	private float levenshteinDistance(final String first, final String second) {
		final LevenshteinDistance levDist = LevenshteinDistance.getDefaultInstance();
		final int dist = levDist.apply(first.toUpperCase(), second.toUpperCase()).intValue();
		int length = larger(first, second).length();
		System.out.println("dist: " + dist + " lenght: " + length);
		return ((float) dist / (float) length);
	}

	private String larger(final String first, final String second) {
		if (first == null && second == null) {
			return "";
		} else if (first != null && second == null) {
			return first;
		} else if (first == null && second != null) {
			return second;
		} else {
			return first.length() >= second.length() ? first : second;
		}
	}

}
