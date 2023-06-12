package com.infamous.all_bark_all_bite.client.renderer;

import com.infamous.all_bark_all_bite.AllBarkAllBite;
import com.infamous.all_bark_all_bite.common.compat.entity.ZombieDog;
import com.infamous.all_bark_all_bite.common.entity.dog.Dog;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class UndeadDogRenderer extends DogRenderer {
    private static final ResourceLocation ZOMBIE_DOG_TEXTURE = new ResourceLocation(AllBarkAllBite.MODID, "textures/entity/dog/zombie_dog.png");
    private static final ResourceLocation SKELETON_DOG_TEXTURE = new ResourceLocation(AllBarkAllBite.MODID, "textures/entity/dog/skeleton_dog.png");

    public UndeadDogRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Dog entity) {
        return entity instanceof ZombieDog ? ZOMBIE_DOG_TEXTURE : SKELETON_DOG_TEXTURE;
    }
}
