package com.example;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

public class HighwayChunkGenerator extends ChunkGenerator {
    
    public static final MapCodec<HighwayChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(generator -> generator.biomeSource)
            ).apply(instance, instance.stable(HighwayChunkGenerator::new))
    );

    public HighwayChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    public void buildSurface(ChunkRegion region, StructureAccessor structures, Chunk chunk) {
        int chunkX = chunk.getPos().x;
        int startX = chunkX * 16;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = startX + x;

                chunk.setBlockState(new BlockPos(x, -64, z), Blocks.BEDROCK.getDefaultState(), false);
                for (int y = -63; y <= -54; y++) { chunk.setBlockState(new BlockPos(x, y, z), Blocks.SAND.getDefaultState(), false); }
                for (int y = -53; y <= -1; y++) { chunk.setBlockState(new BlockPos(x, y, z), Blocks.WATER.getDefaultState(), false); }

                if (worldX >= -4 && worldX < 4) {
                    for (int y = -54; y <= -1; y++) { chunk.setBlockState(new BlockPos(x, y, z), Blocks.STONE_BRICKS.getDefaultState(), false); }
                    chunk.setBlockState(new BlockPos(x, 0, z), Blocks.BLACK_CONCRETE.getDefaultState(), false);

                    if (worldX == -1 || worldX == 0) {
                        int lineZ = chunk.getPos().getStartZ() + z;
                        if (Math.abs(lineZ) % 8 < 4) { chunk.setBlockState(new BlockPos(x, 1, z), Blocks.WHITE_CONCRETE.getDefaultState(), false); }
                    }
                    if (worldX == -4 || worldX == 3) { chunk.setBlockState(new BlockPos(x, 1, z), Blocks.GRAY_CONCRETE.getDefaultState(), false); }
                }
            }
        }
    }

    @Override protected MapCodec<? extends ChunkGenerator> getCodec() { return CODEC; }
    @Override public void carve(ChunkRegion chunkRegion, long seed, net.minecraft.world.gen.noise.NoiseConfig noiseConfig, net.minecraft.world.biome.source.BiomeAccess biomeAccess, StructureAccessor structureAccessor, Chunk chunk, net.minecraft.world.gen.GenerationStep.Carver carverStep) {}
    @Override public void buildSurface(ChunkRegion region, StructureAccessor structures, net.minecraft.world.gen.noise.NoiseConfig noiseConfig, Chunk chunk) { buildSurface(region, structures, chunk); }
    @Override public int getBaseHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) { return 1; }
    @Override public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView world) { return new VerticalBlockSample(0, new net.minecraft.block.BlockState[0]); }
    @Override public int getSeaLevel() { return -1; }
    @Override public int getMinimumY() { return -64; }
    @Override public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world) { return 1; }
}
