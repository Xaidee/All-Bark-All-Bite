package com.infamous.all_bark_all_bite.common.compat.entity;

import com.infamous.all_bark_all_bite.common.entity.dog.Dog;
import com.infamous.all_bark_all_bite.common.compat.PCCompat;
import com.teamabnormals.pet_cemetery.core.other.PCUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class SkeletonDog extends Dog {

    public SkeletonDog(EntityType<? extends SkeletonDog> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Dog.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3F + PCUtil.SPEED_DIFF)
                .add(Attributes.MAX_HEALTH, 8.0D - PCUtil.HEALTH_DIFF)
                .add(Attributes.ATTACK_DAMAGE, 2.0D + PCUtil.DAMAGE_DIFF);
    }

    @Override
    public SkeletonDog getBreedOffspring(ServerLevel level, AgeableMob partner) {
        SkeletonDog offspring = PCCompat.SKELETON_DOG.get().create(level);
        if (partner instanceof Dog mate && offspring != null) {
            if (this.random.nextBoolean()) {
                offspring.setVariant(this.getVariant());
            } else {
                offspring.setVariant(mate.getVariant());
            }

            if (this.isTame()) {
                offspring.setOwnerUUID(this.getOwnerUUID());
                offspring.setTame(true);
                if (this.random.nextBoolean()) {
                    offspring.setCollarColor(this.getCollarColor());
                } else {
                    offspring.setCollarColor(mate.getCollarColor());
                }
            }
        }

        return offspring;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    @Override
    public float getWetShade(float partialTicks) {
        return 1.0F;
    }
}
