package org.pinguin.gf.service.api.journalentry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TagTO {
	private Long tagId;
	private String name;

	public TagTO(final String name) {
		this.name = name;
	}

}
