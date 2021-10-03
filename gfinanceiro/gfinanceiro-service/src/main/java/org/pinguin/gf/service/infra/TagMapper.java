package org.pinguin.gf.service.infra;

import org.pinguin.gf.domain.journalentry.Tag;
import org.pinguin.gf.service.api.journalentry.TagTO;

import fr.xebia.extras.selma.Mapper;

@Mapper(withIgnoreFields = "org.pinguin.gf.domain.journalentry.Tag.entries")
public interface TagMapper {

	TagTO asTO(Tag entity);

	Tag asEntity(TagTO to);

}
