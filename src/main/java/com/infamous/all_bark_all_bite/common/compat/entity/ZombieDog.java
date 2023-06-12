package com.infamous.all_bark_all_bite.common.compat.entity;

import com.infamous.all_bark_all_bite.common.entity.dog.Dog;
import com.infamous.all_bark_all_bite.common.registry.ABABEntityTypes;
import com.infamous.all_bark_all_bite.common.compat.PCCompat;
import com.teamabnormals.pet_cemetery.core.other.PCCriteriaTriggers;
import com.teamabnormals.pet_cemetery.core.other.PCUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.UUID;

public class ZombieDog extends Dog {
    private static final EntityDataAccessor<Boolean> CONVERTING = SynchedEntityData.defineId(ZombieDog.class, EntityDataSerializers.BOOLEAN);
    private int conversionTime;
    private UUID conversionStarter;

    public ZombieDog(EntityType<? extends ZombieDog> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Dog.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3F - PCUtil.SPEED_DIFF)
                .add(Attributes.MAX_HEALTH, 8.0D + PCUtil.HEALTH_DIFF)
                .add(Attributes.ATTACK_DAMAGE, 2.0D - PCUtil.DAMAGE_DIFF);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CONVERTING, false);
    }

    @Override
    public ZombieDog getBreedOffspring(ServerLevel level, AgeableMob partner) {
        ZombieDog offspring = PCCompat.ZOMBIE_DOG.get().create(level);
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
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("ConversionTime", this.isConverting() ? this.conversionTime : -1);
        if (this.conversionStarter != null) {
            compoundTag.putUUID("ConversionPlayer", this.conversionStarter);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("ConversionTime", 99) && compoundTag.getInt("ConversionTime") > -1) {
            this.startConverting(compoundTag.hasUUID("ConversionPlayer") ? compoundTag.getUUID("ConversionPlayer") : null, compoundTag.getInt("ConversionTime"));        }
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && this.isConverting()) {
            int i = PCUtil.getConversionProgress(this);
            this.conversionTime -= i;
            if (this.conversionTime <= 0 && ForgeEventFactory.canLivingConvert(this, ABABEntityTypes.DOG.get(), (timer) -> this.conversionTime = timer)) {
                this.cureZombie((ServerLevel) this.level);
            }
        }

        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == Items.GOLDEN_APPLE) {
            if (this.hasEffect(MobEffects.WEAKNESS)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                if (!this.level.isClientSide) {
                    this.startConverting(player.getUUID(), this.random.nextInt(2401) + 3600);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            return super.mobInteract(player, hand);
        }
    }

    public boolean isConverting() {
        return this.getEntityData().get(CONVERTING);
    }

    private void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn) {
        this.conversionStarter = conversionStarterIn;
        this.conversionTime = conversionTimeIn;
        this.getEntityData().set(CONVERTING, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, conversionTimeIn, Math.min(this.level.getDifficulty().getId() - 1, 0)));
        this.level.broadcastEntityEvent(this, (byte) 16);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 16) {
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, this.getSoundSource(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void cureZombie(ServerLevel level) {
        Dog dog = this.copyEntityData();
        dog.finalizeSpawn(level, level.getCurrentDifficultyAt(dog.blockPosition()), MobSpawnType.CONVERSION, null, null);
        dog.setVariant(this.getVariant());

        if (this.conversionStarter != null) {
            Player player = level.getPlayerByUUID(this.conversionStarter);
            if (player instanceof ServerPlayer serverPlayer) {
                PCCriteriaTriggers.CURED_ZOMBIE_PET.trigger(serverPlayer, this, dog);
            }
        }

        dog.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        if (!this.isSilent()) {
            level.levelEvent(null, 1027, this.blockPosition(), 0);
        }

        ForgeEventFactory.onLivingConvert(this, dog);
    }

    public Dog copyEntityData() {
        Dog dog = this.convertTo(ABABEntityTypes.DOG.get(), false);
        dog.setCollarColor(this.getCollarColor());
        dog.setTame(this.isTame());
        dog.setOrderedToSit(this.isOrderedToSit());
        if (this.getOwner() != null)
            dog.setOwnerUUID(this.getOwner().getUUID());
        return dog;
    }
}
