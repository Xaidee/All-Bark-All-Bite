package com.infamous.call_of_the_wild.common.behavior.pack;

import com.google.common.collect.ImmutableMap;
import com.infamous.call_of_the_wild.common.entity.dog.ai.SharedWolfAi;
import com.infamous.call_of_the_wild.common.registry.ABABMemoryModuleTypes;
import com.infamous.call_of_the_wild.common.util.AiUtil;
import com.infamous.call_of_the_wild.common.util.PackAi;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"NullableProblems", "unused"})
public class StartHowling<E extends LivingEntity> extends Behavior<E> {
    private final UniformInt howlCooldown;
    private final int tooFar;
    private long lastCheckTimestamp;

    public StartHowling(UniformInt howlCooldown, int tooFar) {
        super(ImmutableMap.of(
                ABABMemoryModuleTypes.LEADER.get(), MemoryStatus.REGISTERED,
                ABABMemoryModuleTypes.FOLLOWERS.get(), MemoryStatus.REGISTERED,
                ABABMemoryModuleTypes.HOWLED_RECENTLY.get(), MemoryStatus.VALUE_ABSENT
        ));
        this.howlCooldown = howlCooldown;
        this.tooFar = tooFar;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E mob) {
        if(this.lastCheckTimestamp != 0 && level.getGameTime() - this.lastCheckTimestamp < FollowPackLeader.INTERVAL_TICKS){
            return false;
        } else{
            if(PackAi.isFollower(mob)){
                this.timestampLastCheck(level.getGameTime());

                Optional<LivingEntity> leader = PackAi.getLeader(mob);
                return leader.isPresent() && this.followerTooFar(leader.get(), mob);
            } else if(PackAi.hasFollowers(mob)){
                this.timestampLastCheck(level.getGameTime());

                Set<UUID> followerUUIDs = PackAi.getFollowerUUIDs(mob).get();
                for(UUID followerUUID : followerUUIDs){
                    if(followerUUID.equals(mob.getUUID())) continue; // ignore self
                    Optional<LivingEntity> follower = AiUtil.getLivingEntityFromUUID(level, followerUUID);
                    if(follower.isPresent() && this.followerTooFar(mob, follower.get())){
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
    }

    private void timestampLastCheck(long gameTime) {
        this.lastCheckTimestamp = gameTime;
    }

    private boolean followerTooFar(LivingEntity leader, LivingEntity follower) {
        return !follower.closerThan(leader, this.tooFar);
    }

    @Override
    protected void start(ServerLevel level, E mob, long gameTime) {
        SharedWolfAi.howl(mob);
        int howlCooldownInTicks = this.howlCooldown.sample(mob.getRandom());
        SharedWolfAi.setHowledRecently(mob, howlCooldownInTicks);
        //GenericAi.getNearbyAllies(mob).forEach(le -> SharedWolfAi.setHowledRecently(le, this.howlCooldown.sample(le.getRandom())));
    }
}