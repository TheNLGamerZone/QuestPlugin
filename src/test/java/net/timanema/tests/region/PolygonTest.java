/*
 * Copyright (C) 2019  Tim Anema
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.timanema.tests.region;

import net.timanema.questplugin.area.Polygon;
import net.timanema.questplugin.utils.LocationWithID;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.LinkedHashSet;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class PolygonTest
{
    private Object testObject;
    private Polygon testPolygon;
    private Polygon regularPolygon;
    private LocationWithID locationInPolygon;
    private LocationWithID locationInPolygonTightFit;
    private LocationWithID locationNotInPolygon;
    private LocationWithID locationNotInPolygonOutsideBorderXMin;
    private LocationWithID locationNotInPolygonOutsideBorderXMax;
    private LocationWithID locationNotInPolygonOutsideBorderYMin;
    private LocationWithID locationNotInPolygonOutsideBorderYMax;
    private LocationWithID locationNotInWorld;

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
        LinkedHashSet<LocationWithID> locations = new LinkedHashSet<>();

        locations.add(new LocationWithID(UUID.randomUUID(), world1, 15, 16, 0));
        locations.add(new LocationWithID(UUID.randomUUID(), world1, 18.34, 17, 0));
        locations.add(new LocationWithID(UUID.randomUUID(), world1, 21.4, 20.123, 0));
        locations.add(new LocationWithID(UUID.randomUUID(), world1, 17.2, 23.4, 0));
        locations.add(new LocationWithID(UUID.randomUUID(), world1, 8.2, 17, 0));
        locations.add(new LocationWithID(UUID.randomUUID(), world1, 13.32, 18.2, 0));

        locationInPolygon = new LocationWithID(UUID.randomUUID(), world1, 17, 19, 0);
        locationInPolygonTightFit = new LocationWithID(UUID.randomUUID(), world1, 11, 18.3, 0);
        locationNotInPolygon = new LocationWithID(UUID.randomUUID(), world1, 12.7, 16.8, 0);
        locationNotInPolygonOutsideBorderXMin = new LocationWithID(UUID.randomUUID(), world1, 0, 17, 0);
        locationNotInPolygonOutsideBorderXMax = new LocationWithID(UUID.randomUUID(), world1, 42, 17, 0);
        locationNotInPolygonOutsideBorderYMin = new LocationWithID(UUID.randomUUID(), world1, 16, 15, 0);
        locationNotInPolygonOutsideBorderYMax = new LocationWithID(UUID.randomUUID(), world1, 16, 42, 0);
        locationNotInWorld = new LocationWithID(UUID.randomUUID(), world2, 17, 19, 0);

        UUID uuid = UUID.randomUUID();

        regularPolygon = new Polygon(uuid, locations);
        testPolygon = new Polygon(uuid, locations);
    }

    @Test
    public void polygon_equals_test_equal()
    {
        assertEquals("These objects are equal", regularPolygon, testPolygon);
    }

    @Test
    public void polygon_equals_test_not_instance_of()
    {
        assertFalse("Polygon should check instance", regularPolygon.equals(testObject));
    }

    @Test
    public void polygon_equals_test_same_instance()
    {
        assertEquals("These object are equal", regularPolygon, regularPolygon);
    }

    @Test
    public void polygon_equals_test_not_equal()
    {
        assertFalse("These objects are not equal",
                regularPolygon.equals(new Polygon(UUID.randomUUID(), new LinkedHashSet<>())));
    }

    @Test
    public void polygon_hashcode_equal()
    {
        assertEquals("These objects are equal (hashcode)", regularPolygon.hashCode(), testPolygon.hashCode());
    }

    @Test
    public void polygon_hashcode_not_equal()
    {
        assertFalse("These objects are not equal (hashcode)",
                regularPolygon.hashCode() == new Polygon(UUID.randomUUID(), new LinkedHashSet<>()).hashCode());
    }

    @Test
    public void polygon_in_region_valid()
    {
        assertTrue("Should be in polygon", regularPolygon.inRegion(locationInPolygon, true));
    }

    @Test
    public void polygon_in_region_valid_just_barely()
    {
        assertTrue("Should be in polygon", regularPolygon.inRegion(locationInPolygonTightFit, true));
    }

    @Test
    public void polygon_in_region_invalid_within_borders()
    {
        assertFalse("Should not be in polygon", regularPolygon.inRegion(locationNotInPolygon, true));
    }

    @Test
    public void polygon_in_region_invalid_outside_borders_xmin()
    {
        assertFalse("Should not be in polygon", regularPolygon.inRegion(locationNotInPolygonOutsideBorderXMin, true));
    }
    @Test
    public void polygon_in_region_invalid_outside_borders_xmax()
    {
        assertFalse("Should not be in polygon", regularPolygon.inRegion(locationNotInPolygonOutsideBorderXMax, true));
    }
    @Test
    public void polygon_in_region_invalid_outside_borders_ymin()
    {
        assertFalse("Should not be in polygon", regularPolygon.inRegion(locationNotInPolygonOutsideBorderYMin, true));
    }
    @Test
    public void polygon_in_region_invalid_outside_borders_ymax()
    {
        assertFalse("Should not be in polygon", regularPolygon.inRegion(locationNotInPolygonOutsideBorderYMax, true));
    }

    @Test
    public void polygon_in_region_invalid_other_world()
    {
        assertFalse("Should not be in polygon (other world)", regularPolygon.inRegion(locationNotInWorld, true));
    }
}
