package com.bobmowzie.mowziesmobs.server.ai.animation;

import com.bobmowzie.mowziesmobs.server.entity.MowzieEntity;
import net.ilexiconn.llibrary.server.animation.Animation;
import net.ilexiconn.llibrary.server.animation.IAnimatedEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundEvent;

import java.util.List;

public class AnimationAreaAttackAI<T extends MowzieEntity & IAnimatedEntity> extends AnimationAttackAI<T> {
    private float arc;
    private float height;
    private boolean faceTarget;

    public AnimationAreaAttackAI(T entity, Animation animation, SoundEvent attackSound, SoundEvent hitSound, float knockback, float range, float height, float arc, float damageMultiplier, int damageFrame) {
        super(entity, animation, attackSound, hitSound, knockback, range, damageMultiplier, damageFrame);
        this.arc = arc;
        this.height = height;
        this.faceTarget = true;
    }

    public AnimationAreaAttackAI(T entity, Animation animation, SoundEvent attackSound, SoundEvent hitSound, float knockback, float range, float height, float arc, float damageMultiplier, int damageFrame, boolean faceTarget) {
        super(entity, animation, attackSound, hitSound, knockback, range, damageMultiplier, damageFrame);
        this.arc = arc;
        this.height = height;
        this.faceTarget = faceTarget;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
    }

    @Override
    public void updateTask() {
        if (faceTarget && entity.getAnimationTick() < damageFrame && entityTarget != null) {
            entity.faceEntity(entityTarget, 30F, 30F);
        }
        else if (entity.getAnimationTick() == damageFrame) {
            hitEntities();
        }
    }

    public void hitEntities() {
        List<EntityLivingBase> entitiesHit = entity.getEntityLivingBaseNearby(range, height, range, range);
        boolean hit = false;
        for (EntityLivingBase entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.posZ - entity.posZ, entityHit.posX - entity.posX) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = entity.renderYawOffset % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.posZ - entity.posZ) * (entityHit.posZ - entity.posZ) + (entityHit.posX - entity.posX) * (entityHit.posX - entity.posX)) - entityHit.width / 2f;
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                entity.attackEntityAsMob(entityHit, damageMultiplier);
                entityHit.motionX *= knockback;
                entityHit.motionZ *= knockback;
                hit = true;
            }
        }
        if (hit && hitSound != null) {
            entity.playSound(hitSound, 1, 1);
        }
        if (attackSound != null) entity.playSound(attackSound, 1, 1);
    }
}
