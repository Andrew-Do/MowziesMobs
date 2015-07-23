package com.bobmowzie.mowziesmobs.client.model.entity;

import thehippomaster.AnimationAPI.AnimationAPI;

import com.bobmowzie.mowziesmobs.common.item.ItemWroughtAxe;
import com.bobmowzie.mowziesmobs.common.property.WroughtAxeSwingProperty;

import net.ilexiconn.llibrary.client.render.IModelExtension;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by jnad325 on 7/7/15.
 */
public class ModelPlayerExtension extends ModelBiped implements IModelExtension
{
    private float partialTicks = 0;
    @Override
    public void setRotationAngles(ModelBiped modelBiped, float v, float v1, float v2, float v3, float v4, float v5, Entity entity)
    {
        if (((EntityPlayer) entity).getHeldItem() != null && ((EntityPlayer) entity).getHeldItem().getItem() instanceof ItemWroughtAxe)
        {
            float timer = WroughtAxeSwingProperty.getProperty((EntityPlayer) entity).getTime(AnimationAPI.proxy.getPartialTick());
            if (timer > 0)
            {
                float controller1 = WroughtAxeSwingProperty.fnc1(timer);
                float controller2 = WroughtAxeSwingProperty.fnc2(timer);
                float controller3 = WroughtAxeSwingProperty.fnc3(timer, 0.1f, 0.9f, 30);
                modelBiped.bipedRightArm.rotateAngleY = 0.6F * controller1 + 0.3F * controller2;
                modelBiped.bipedRightArm.rotateAngleX = (float) (-Math.PI / 2 * controller3);
            }
        }
    }

    @Override
    public void preRender(EntityPlayer entityPlayer, ModelBase modelBase, float v)
    {
    }

    @Override
    public void postRender(EntityPlayer entityPlayer, ModelBase modelBase, float v)
    {
    }

    @Override
    public void init(ModelBase modelBase)
    {
    }
}