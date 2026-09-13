package etcodehome.freeterraforged.world.worldgen.cell.terrain.populator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import etcodehome.freeterraforged.world.worldgen.cell.heightmap.Levels;

class ArchipelagoPopulatorTest {
    private static final float EPSILON = 1.0E-6F;

    @Test
    void beachWidthUsesTheCompletePresetEditorRange() {
        assertEquals(0.05F, ArchipelagoPopulator.beachWidth(0.0F), EPSILON);
        assertEquals(0.50F, ArchipelagoPopulator.beachWidth(0.50F), EPSILON);
        assertEquals(0.50F, ArchipelagoPopulator.beachWidth(1.0F), EPSILON);
    }

    @Test
    void oceanFloorDetailCannotCrossTheWaterline() {
        Levels levels = new Levels(256, 1024, 1024, 63);

        float height = ArchipelagoPopulator.addSubmergedDetail(levels.water(-4), 120.0F, levels);

        assertEquals(levels.water, height, EPSILON);
        assertEquals(levels.waterLevel - 1, levels.scale(height));
    }

    @Test
    void oceanFloorDetailRetainsItsBlockScaleBelowTheWaterline() {
        Levels levels = new Levels(256, 1024, 1024, 63);

        float height = ArchipelagoPopulator.addSubmergedDetail(levels.water(-10), 4.0F, levels);

        assertEquals(levels.water(-6), height, EPSILON);
    }

    @Test
    void continentEdgeStartsFromTheAmbientContinentalness() {
        assertEquals(0.04F, ArchipelagoPopulator.continentEdge(0.0F, 0.5F, 0.04F, 0.10F), EPSILON);
        assertEquals(0.10F, ArchipelagoPopulator.continentEdge(0.5F, 0.5F, 0.04F, 0.10F), EPSILON);
    }
}
