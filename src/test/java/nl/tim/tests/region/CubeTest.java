package nl.tim.tests.region;

import nl.tim.questplugin.area.Cube;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class CubeTest
{
    private Object testObject;
    private Cube regularCube;
    private Cube testCube;
    private Location locationInCube;
    private Location locationNotInCube;
    private Location locationInCubeIgnoreHeight;
    private Location locationNotInCubeIgnoreHeight;
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
        Location location1 = new Location(world1, 1, 5, 0);
        Location location2 = new Location(world1, 7.32, 24.2, 25);

        locationInCube = new Location(world1, 5, 10, 12);
        locationNotInCube = new Location(world1, 5, 10, 26);
        locationInCubeIgnoreHeight = new Location(world1, 2, 17, -3);
        locationNotInCubeIgnoreHeight = new Location(world1, 5, -2, -12.3);
        locationInOtherWorld = new Location(world2, 5, 10 , 12);

        // Creating objects
        UUID uuid = UUID.randomUUID();
        regularCube = new Cube(uuid, location1, location2);
        testCube = new Cube(uuid, location1, location2);
    }

    @Test
    public void cube_equals_test_equal()
    {
        assertEquals("These objects are equal", regularCube, testCube);
    }

    @Test
    public void cube_equals_test_not_instance_of()
    {
        assertFalse("Cube should check instance", regularCube.equals(testObject));
    }

    @Test
    public void cube_equals_test_equal_same_instance()
    {
        assertEquals("These objects are equal", regularCube, regularCube);
    }

    @Test
    public void cube_equals_test_not_equal()
    {
        assertFalse("These objects are not equal", regularCube.equals(new Cube(UUID.randomUUID(), locationInCube, locationNotInCube)));
    }

    @Test
    public void cube_hashcode_equal()
    {
        assertEquals("These objects are equal (hashcode)", regularCube.hashCode(), testCube.hashCode());
    }

    @Test
    public void cube_hashcode_not_equal()
    {
        assertFalse("These objects are not equal (hashcode)",
                regularCube.hashCode() ==
                        new Cube(UUID.randomUUID(), new Location(null, 0, 0, 0), new Location(null, 0, 0, 1)).hashCode());
    }

    @Test
    public void cube_in_region_valid()
    {
        assertTrue("Should be in Cube", regularCube.inRegion(locationInCube, false));
    }

    @Test
    public void cube_in_region_invalid_height()
    {
        assertFalse("Should not be in Cube (z-axis)", regularCube.inRegion(locationNotInCube, false));
    }

    @Test
    public void cube_in_region_valid_ignore_height()
    {
        assertTrue("Should be in Cube", regularCube.inRegion(locationInCubeIgnoreHeight, true));
    }

    @Test
    public void cube_in_region_invalid_y_ignore_height()
    {
        assertFalse("Should not be in Cube (y-axis)", regularCube.inRegion(locationNotInCubeIgnoreHeight, true));
    }

    @Test
    public void cube_in_region_invalid_other_world()
    {
        assertFalse("Should not be in Cube (other world)", regularCube.inRegion(locationInOtherWorld, false));
    }
}
