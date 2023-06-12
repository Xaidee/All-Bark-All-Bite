package com.infamous.all_bark_all_bite.common.compat;

import com.infamous.all_bark_all_bite.common.registry.ABABEntityTypes;
import com.infamous.all_bark_all_bite.common.registry.ABABItems;
import com.infamous.all_bark_all_bite.common.compat.entity.SkeletonDog;
import com.infamous.all_bark_all_bite.common.compat.entity.ZombieDog;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

public class PCCompat {

    public static final String ZOMBIE_DOG_NAME = "zombie_dog";
    public static final RegistryObject<EntityType<ZombieDog>> ZOMBIE_DOG = ABABEntityTypes.ENTITY_TYPES.register(
            ZOMBIE_DOG_NAME,
            () -> EntityType.Builder.of(ZombieDog::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(10)
                    .build(ZOMBIE_DOG_NAME)
    );
    public static final String SKELETON_DOG_NAME = "skeleton_dog";
    public static final RegistryObject<EntityType<SkeletonDog>> SKELETON_DOG = ABABEntityTypes.ENTITY_TYPES.register(
            SKELETON_DOG_NAME,
            () -> EntityType.Builder.of(SkeletonDog::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.85F)
                    .clientTrackingRange(10)
                    .build(SKELETON_DOG_NAME)
    );

    public static final RegistryObject<Item> ZOMBIE_DOG_SPAWN_EGG = ABABItems.ITEMS.register(String.format("%s_spawn_egg", ZOMBIE_DOG_NAME),
            () -> new ForgeSpawnEggItem(ZOMBIE_DOG, 0x6A9D5A, 0x364430, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> SKELETON_DOG_SPAWN_EGG = ABABItems.ITEMS.register(String.format("%s_spawn_egg", SKELETON_DOG_NAME),
            () -> new ForgeSpawnEggItem(SKELETON_DOG, 0x979797, 0x494949, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));


    // Function to load class, should only be loaded when Pet Cemetery is installed to avoid crashes from missing classes
    public static void loadClass() {}

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ZOMBIE_DOG.get(), ZombieDog.createAttributes().build());
        event.put(SKELETON_DOG.get(), SkeletonDog.createAttributes().build());
    }
}