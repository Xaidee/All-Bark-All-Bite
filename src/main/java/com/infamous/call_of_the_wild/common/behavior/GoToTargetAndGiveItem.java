package com.infamous.call_of_the_wild.common.behavior;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("NullableProblems")
public class GoToTargetAndGiveItem<E extends LivingEntity> extends Behavior<E> {
   private final Function<E, ItemStack> itemGetter;
   private final Function<E, Optional<PositionTracker>> targetPositionGetter;
   private final float speedModifier;
   private final int closeEnough;
   private final int itemPickupCooldown;
   private final Consumer<E> onThrown;

   public GoToTargetAndGiveItem(Function<E, ItemStack> itemGetter, Function<E, Optional<PositionTracker>> targetPositionGetter, float speedModifier, int closeEnough, int itemPickupCooldown, Consumer<E> onThrown) {
      super(Map.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.REGISTERED));
      this.itemGetter = itemGetter;
      this.targetPositionGetter = targetPositionGetter;
      this.speedModifier = speedModifier;
      this.closeEnough = closeEnough;
      this.itemPickupCooldown = itemPickupCooldown;
      this.onThrown = onThrown;
   }

   @Override
   protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
      return this.canThrowItemToTarget(mob);
   }

   @Override
   protected void start(ServerLevel level, E mob, long gameTime) {
      this.goToTarget(mob);
   }

   private void goToTarget(E mob) {
      this.targetPositionGetter.apply(mob).ifPresent((pt) -> BehaviorUtils.setWalkAndLookTargetMemories(mob, pt, this.speedModifier, this.closeEnough));
   }

   @Override
   protected void tick(ServerLevel level, E mob, long gameTime) {
      Optional<PositionTracker> maybePositionTracker = this.targetPositionGetter.apply(mob);
      if (maybePositionTracker.isPresent()) {
         PositionTracker positionTracker = maybePositionTracker.get();
         double distanceToMob = positionTracker.currentPosition().distanceTo(mob.getEyePosition());
         if (distanceToMob < this.closeEnough) {
            ItemStack toThrow = this.itemGetter.apply(mob).split(1);
            if (!toThrow.isEmpty()) {
               throwItem(mob, toThrow, getThrowPosition(positionTracker));
               this.onThrown.accept(mob);
               mob.getBrain().setMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, this.itemPickupCooldown);
            }
         } else{
            this.goToTarget(mob);
         }
      }
   }

   @Override
   protected boolean canStillUse(ServerLevel level, E mob, long gameTime) {
      return this.canThrowItemToTarget(mob);
   }

   private boolean canThrowItemToTarget(E mob) {
      if (this.itemGetter.apply(mob).isEmpty()) {
         return false;
      } else {
         Optional<PositionTracker> optional = this.targetPositionGetter.apply(mob);
         return optional.isPresent();
      }
   }

   private static Vec3 getThrowPosition(PositionTracker positionTracker) {
      return positionTracker.currentPosition().add(0.0D, 1.0D, 0.0D);
   }

   public static void throwItem(LivingEntity mob, ItemStack stack, Vec3 throwPosition) {
      Vec3 vec3 = new Vec3(0.2F, 0.3F, 0.2F);
      BehaviorUtils.throwItem(mob, stack, throwPosition, vec3, 0.2F);
   }
}