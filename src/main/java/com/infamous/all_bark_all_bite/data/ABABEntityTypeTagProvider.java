package com.infamous.all_bark_all_bite.data;

import com.infamous.all_bark_all_bite.AllBarkAllBite;
import com.infamous.all_bark_all_bite.common.ABABTags;
import com.infamous.all_bark_all_bite.common.compat.CompatUtil;
import com.infamous.all_bark_all_bite.common.compat.FDCompat;
import com.infamous.all_bark_all_bite.common.compat.PCCompat;
import com.infamous.all_bark_all_bite.common.registry.ABABEntityTypes;
import com.teamabnormals.pet_cemetery.core.other.tags.PCEntityTypeTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ABABEntityTypeTagProvider extends EntityTypeTagsProvider {
    public ABABEntityTypeTagProvider(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, AllBarkAllBite.MODID, existingFileHelper);
    }

    public static ABABEntityTypeTagProvider create(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper){
        return new ABABEntityTypeTagProvider(generator, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(ABABTags.DOG_ALWAYS_HOSTILES).addTag(EntityTypeTags.SKELETONS);
        this.tag(ABABTags.DOG_HUNT_TARGETS).add(EntityType.RABBIT, EntityType.CHICKEN);
        this.tag(ABABTags.DOG_DISLIKED);

        this.tag(ABABTags.WOLF_ALWAYS_HOSTILES).addTag(EntityTypeTags.SKELETONS).add(EntityType.FOX);
        this.tag(ABABTags.WOLF_HUNT_TARGETS).add(EntityType.SHEEP, EntityType.GOAT, EntityType.PIG, EntityType.RABBIT, EntityType.CHICKEN);
        this.tag(ABABTags.WOLF_DISLIKED).add(EntityType.LLAMA).add(EntityType.TRADER_LLAMA);

        this.tag(ABABTags.ILLAGER_HOUND_ALWAYS_HOSTILES).add(EntityType.VILLAGER).add(EntityType.WANDERING_TRADER).add(EntityType.IRON_GOLEM);

        this.tag(FDCompat.DOG_FOOD_USERS).add(ABABEntityTypes.DOG.get());

        if (CompatUtil.isPCLoaded()) {
            this.tag(PCEntityTypeTags.DROPS_PET_COLLAR).add(ABABEntityTypes.DOG.get());
            this.tag(PCEntityTypeTags.ZOMBIE_PETS).add(PCCompat.ZOMBIE_DOG.get());
            this.tag(PCEntityTypeTags.SKELETON_PETS).add(PCCompat.SKELETON_DOG.get());
            this.tag(FDCompat.DOG_FOOD_USERS).add(PCCompat.ZOMBIE_DOG.get()).add(PCCompat.SKELETON_DOG.get());
        }
    }
}
