package me.philnate.textmanager.entities;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.data.mongodb.core.mapping.Document;

public class _Entitiy {

    @Test
    public void testDocumentAnnotationPrsent() {
	assertTrue("Entity is missing @Document annotation", new Entity()
		.getClass().isAnnotationPresent(Document.class));
    }
}
