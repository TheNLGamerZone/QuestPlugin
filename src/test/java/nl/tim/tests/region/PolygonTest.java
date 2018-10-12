package nl.tim.tests.region;

import nl.tim.questplugin.area.Polygon;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.LinkedHashSet;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class PolygonTest
{
    private Object testObject;
    private Polygon testPolygon;
    private Polygon regularPolygon;
    private Location locationInPolygon;
    private Location locationInPolygonTightFit;
    private Location locationNotInPolygon;
    private Location locationNotInPolygonOutsideBorder;
    private Location locationNotInWorld;

    @Before
    public void setup()
    {
        // Mock worlds
        World world1 = PowerMockito.mock(World.class);
        World world2 = PowerMockito.mock(World.class);

        when(world1.getName()).thenReturn("world1");
        when(world2.getName()).thenReturn("world2");

        // Create test object
        testObject = new Object();

        // Create polygon locations
        LinkedHashSet<Location> locations = new LinkedHashSet<>();

        locations.add(new Location(world1, 15, 16, 0));
        locations.add(new Location(world1, 18.34, 17, 0));
        locations.add(new Location(world1, 21.4, 20.123, 0));
        locations.add(new Location(world1, 17.2, 23.4, 0));
        locations.add(new Location(world1, 8.2, 17, 0));
        locations.add(new Location(world1, 13.32, 18.2, 0));

        locationInPolygon = new Location(world1, 17, 19, 0);
        locationInPolygonTightFit = new Location(world1, 11, 18.3, 0);
        locationNotInPolygon = new Location(world1, 12.7, 16.8, 0);
        locationNotInPolygonOutsideBorder = new Location(world1, 42, 42, 0);
        locationNotInWorld = new Location(world2, 17, 19, 0);

        UUID uuid = UUID.randomUUID();

        regularPolygon = new Polygon(uuid, locations);
        testPolygon = new Polygon(uuid, locations);
    }

    @Test
    public void polygon_equals_test()
    {
        assertFalse("Polygon should check instance", regularPolygon.equals(testObject));
        assertTrue("These object are equal", regularPolygon.equals(regularPolygon));
        assertTrue("These objects are equal", regularPolygon.equals(testPolygon));
        assertFalse("These objects are not equal", regularPolygon.equals(new Polygon(UUID.randomUUID(), new LinkedHashSet<>())));
    }

    @Test
    public void polygon_in_region()
    {
        assertTrue("Should be in polygon", regularPolygon.inRegion(locationInPolygon, true));
        assertTrue("Should be in polygon", regularPolygon.inRegion(locationInPolygonTightFit, true));
        assertFalse("Should not be in polygon", regularPolygon.inRegion(locationNotInPolygon, true));
        assertFalse("Should not be in polygon", regularPolygon.inRegion(locationNotInPolygonOutsideBorder, true));
        assertFalse("Should not be in polygon (other world)", regularPolygon.inRegion(locationNotInWorld, true));
    }
}
