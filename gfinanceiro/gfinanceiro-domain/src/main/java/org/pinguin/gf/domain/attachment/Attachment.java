package org.pinguin.gf.domain.attachment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Anexo.
 */
@Entity
@Data
@NoArgsConstructor
public class Attachment {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long attachmentId;
	private Long entryId;
	private String fileName;
	@Lob
	private byte[] content;

	/**
	 * Construtor.
	 * 
	 * @param entry
	 * @param fileName
	 * @param content
	 */
	public Attachment(final Long entryId, final String fileName, final byte[] content) {
		this.entryId = entryId;
		this.fileName = fileName;
		this.content = content;
	}

}
