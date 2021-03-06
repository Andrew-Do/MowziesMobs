package com.bobmowzie.mowziesmobs.server.ai;

import com.bobmowzie.mowziesmobs.server.item.BarakoaMask;
import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;

import java.util.Collections;
import java.util.List;

public class BarakoaAttackTargetAI extends EntityAITarget {
    private Class<? extends Entity> targetClass;
    private int targetChance;
    private EntityAINearestAttackableTarget.Sorter attackableTargetSorter;
    private Predicate<Entity> targetEntitySelector;
    private EntityLivingBase targetEntity;
    private boolean useVerticalDistance;

    public BarakoaAttackTargetAI(EntityCreature entity, Class<? extends Entity> targetClass, int targetChance, boolean shouldCheckSight, boolean useVerticalDistance) {
        super(entity, shouldCheckSight, false);
        this.targetClass = targetClass;
        this.targetChance = targetChance;
        this.useVerticalDistance = useVerticalDistance;
        this.attackableTargetSorter = new EntityAINearestAttackableTarget.Sorter(entity);
        this.setMutexBits(1);
        this.targetEntitySelector = target -> {
            if (target instanceof EntityPlayer) {
                if (entity.world.getDifficulty() == EnumDifficulty.PEACEFUL) return false;
                ItemStack headArmorStack = ((EntityPlayer) target).inventory.armorInventory.get(3);
                if (headArmorStack.getItem() instanceof BarakoaMask) {
                    return false;
                }
            }
            return target instanceof EntityLivingBase && BarakoaAttackTargetAI.this.isSuitableTarget((EntityLivingBase) target, false);
        };
    }

    @Override
    public boolean shouldExecute() {
        if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
            return false;
        } else {
            double targetDistance = this.getTargetDistance();
            double distanceY = 4.0D;
            if (useVerticalDistance) distanceY = targetDistance;
            List list = this.taskOwner.world.getEntitiesWithinAABB(this.targetClass, this.taskOwner.getEntityBoundingBox().grow(targetDistance, distanceY, targetDistance), this.targetEntitySelector);
            Collections.sort(list, this.attackableTargetSorter);

            if (list.isEmpty()) {
                return false;
            } else {
                this.targetEntity = (EntityLivingBase) list.get(0);
                return true;
            }
        }
    }

    @Override
    public void startExecuting() {
        this.taskOwner.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
}
