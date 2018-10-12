package nl.tim.tests.region;

import nl.tim.questplugin.area.Sphere;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class SphereTest
{
    private Object testObject;
    private Sphere regularSphere;
    private Sphere testSphere;
    private Location locationInSphere;
    private Location locationNotInSphere;
    private Location locationInSphereIgnoreHeight;
    private Location locationNotInSphereIgnoreHeight;
    private Location locationInOtherWorld;

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

        // Creating locations
        Location center = new Location(world1, 15, 15, 15);
        double radius = 5.5;

        locationInSphere = new Location(world1, 15, 15, 15 + 4);
        locationNotInSphere = new Location(world1, 15, 15, 15 + 7);
        locationInSphereIgnoreHeight = new Location(world1, 15 - 1, 15 + 2, 15 - 10);
        locationNotInSphereIgnoreHeight = new Location(world1, 15 - 10, 15 - 2, 15 - 20);
        locationInOtherWorld = new Location(world2, 15, 15 , 15);

        // Creating objects
        UUID uuid = UUID.randomUUID();
        regularSphere = new Sphere(uuid, center, radius);
        testSphere = new Sphere(uuid, center, radius);
    }

    @Test
    public void cube_equals_test()
    {
        assertFalse("Sphere should check instance", regularSphere.equals(testObject));
        assertTrue("These objects are equal", regularSphere.equals(regularSphere));
        assertTrue("These objects are equal", regularSphere.equals(testSphere));
        assertFalse("These objects are not equal", regularSphere.equals(new Sphere(UUID.randomUUID(), new Location(null, 0 ,0 ,0), 1)));
    }

    @Test
    public void cube_in_region()
    {
        assertTrue("Should be in Sphere", regularSphere.inRegion(locationInSphere, false));
        assertFalse("Should not be in Sphere (z-axis)", regularSphere.inRegion(locationNotInSphere, false));
        assertTrue("Should be in Sphere", regularSphere.inRegion(locationInSphereIgnoreHeight, true));
        assertFalse("Should not be in Sphere (x-axis)", regularSphere.inRegion(locationNotInSphereIgnoreHeight, true));
        assertFalse("Should not be in Sphere (other world)", regularSphere.inRegion(locationInOtherWorld, false));
    }
}
