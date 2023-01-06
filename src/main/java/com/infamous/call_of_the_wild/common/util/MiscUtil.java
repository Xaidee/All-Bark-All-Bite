package com.infamous.call_of_the_wild.common.util;

import com.google.common.collect.Iterables;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class MiscUtil {

    public static <T> T getRandomObject(Collection<T> from, RandomSource randomSource) {
        int index = randomSource.nextInt(from.size());
        return Iterables.get(from, index);
    }

    public static ItemStack removeOneItemFromItemEntity(ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        ItemStack singleton = stack.split(1);
        if (stack.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(stack);
        }

        return singleton;
    }

    @SuppressWarnings("unused")
    public static void addParticlesAroundSelf(LivingEntity livingEntity, ParticleOptions particleOptions, int numParticles, double speedMultiplier, double widthScale, double yOffset) {
        for(int i = 0; i < numParticles; ++i) {
            RandomSource random = livingEntity.getRandom();
            double xSpeed = random.nextGaussian() * speedMultiplier;
            double ySpeed = random.nextGaussian() * speedMultiplier;
            double zSpeed = random.nextGaussian() * speedMultiplier;
            livingEntity.level.addParticle(particleOptions,
                    livingEntity.getRandomX(widthScale),
                    livingEntity.getRandomY() + yOffset,
                    livingEntity.getRandomZ(widthScale),
                    xSpeed,
                    ySpeed,
                    zSpeed);
        }
    }

    public static void addParticlesAroundSelf(LivingEntity livingEntity, ParticleOptions particleOptions, int numParticles, double xSpeed, double ySpeed, double zSpeed, double widthScale, double yOffset) {
        for(int i = 0; i < numParticles; ++i) {
            livingEntity.level.addParticle(particleOptions,
                    livingEntity.getRandomX(widthScale),
                    livingEntity.getRandomY() + yOffset,
                    livingEntity.getRandomZ(widthScale),
                    xSpeed,
                    ySpeed,
                    zSpeed);
        }
    }

    public static void sendParticlesAroundSelf(ServerLevel serverLevel, LivingEntity livingEntity, ParticleOptions particleOptions, double yOffset, int numParticles, double speedMultiplier) {
        serverLevel.sendParticles(particleOptions, livingEntity.getX(), livingEntity.getY() + yOffset, livingEntity.getZ(), numParticles, 0.0D, 0.0D, 0.0D, speedMultiplier);
    }
}