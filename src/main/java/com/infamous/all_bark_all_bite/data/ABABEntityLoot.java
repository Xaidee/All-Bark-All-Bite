package com.infamous.all_bark_all_bite.data;

import com.infamous.all_bark_all_bite.common.ABABTags;
import com.infamous.all_bark_all_bite.common.compat.CompatUtil;
import com.infamous.all_bark_all_bite.common.compat.PCCompat;
import com.infamous.all_bark_all_bite.common.registry.ABABEntityTypes;
import com.infamous.all_bark_all_bite.common.registry.ABABItems;
import net.minecraft.data.loot.EntityLoot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetInstrumentFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.stream.Collectors;

public class ABABEntityLoot extends EntityLoot {

    @Override
    protected void addTables() {
        this.add(ABABEntityTypes.DOG.get(), LootTable.lootTable());
        this.add(ABABEntityTypes.ILLAGER_HOUND.get(), LootTable.lootTable());
        this.add(ABABEntityTypes.HOUNDMASTER.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(ABABItems.WHISTLE.get()))
                        .apply(SetInstrumentFunction.setInstrumentOptions(ABABTags.WHISTLES)))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                        .add(LootItem.lootTableItem(Items.EMERALD)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 1.0F)))
                                .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F))))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())));

        if (CompatUtil.isPCLoaded()) {
            this.add(PCCompat.ZOMBIE_DOG.get(), LootTable.lootTable());
            this.add(PCCompat.SKELETON_DOG.get(), LootTable.lootTable());
        }
    }

    @Override
    protected Iterable<EntityType<?>> getKnownEntities() {
        return ABABEntityTypes.ENTITY_TYPES.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
    }
}
