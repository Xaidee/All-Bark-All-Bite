package com.infamous.call_of_the_wild.common.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class BrainUtil {

    private static final String LIVING_ENTITY_BRAIN = "f_20939_";

    public static <T extends LivingEntity> Brain.Provider<?> brainProvider(Collection<? extends MemoryModuleType<?>> memoryTypes, Collection<? extends SensorType<? extends Sensor<? super T>>> sensorTypes) {
        return Brain.provider(memoryTypes, sensorTypes);
    }

    public static <T extends LivingEntity> Brain<T> makeBrain(Collection<? extends MemoryModuleType<?>> memoryTypes, Collection<? extends SensorType<? extends Sensor<? super T>>> sensorTypes, Dynamic<?> dynamic) {
        return (Brain<T>) brainProvider(memoryTypes, sensorTypes).makeBrain(dynamic);
    }

    public static <T extends LivingEntity> void replaceBrain(T livingEntity, ServerLevel level, Brain<T> replacement, boolean merge) {
        Brain<T> original = (Brain<T>) livingEntity.getBrain();
        original.stopAll(level, livingEntity);
        if(merge) mergeMemories(original, replacement);
        ReflectionUtil.setField(LIVING_ENTITY_BRAIN, LivingEntity.class, livingEntity, replacement);
    }

    @SuppressWarnings("deprecation")
    public static void mergeMemories(Brain<?> original, Brain<?> replacement) {
        Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> originalMemories = original.getMemories();
        Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> replacementMemories = replacement.getMemories();

        originalMemories.forEach((k, v) -> {
            if (v.isPresent()) {
                replacementMemories.put(k, v);
            }
        });
    }

    public static Dynamic<Tag> makeDynamic(NbtOps nbtOps) {
        return new Dynamic<>(nbtOps, nbtOps.createMap(ImmutableMap.of(nbtOps.createString("memories"), nbtOps.emptyMap())));
    }
}
