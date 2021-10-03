package org.pinguin.gf.service.api.journalentry;

import java.util.List;

public interface TagService {

	TagTO createTag(TagTO tag);

	TagTO updateTag(Long id, TagTO tag);

	TagTO deleteTag(Long id);

	TagTO retrieveById(Long id);

	List<TagTO> retrieveAll();

}
