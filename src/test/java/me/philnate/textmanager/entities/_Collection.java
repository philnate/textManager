package me.philnate.textmanager.entities;

import static org.junit.Assert.assertEquals;
import me.philnate.textmanager.MongoBase;
import me.philnate.textmanager.entities.annotations.Collection;

import org.junit.Test;

/**
 * tests around the Collection Annotation
 * 
 * @author philnate
 * 
 */
public class _Collection extends MongoBase {

    @Test
    public void testNameResolution() {
	assertEquals("entity", Entities.getCollectionName(Entity.class));
	assertEquals("camelCase", Entities.getCollectionName(CamelCase.class));
    }

    @Test
    public void testNameResolutionAnnotation() {
	assertEquals("myCollection",
		Entities.getCollectionName(DifferentName.class));
    }

    private static interface CamelCase extends Entity {
    }

    // eventual white spaces should be trimmed
    @Collection(name = " myCollection ")
    private static interface DifferentName extends Entity {
    }

}
