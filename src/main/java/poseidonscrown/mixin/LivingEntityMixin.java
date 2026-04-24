package poseidonscrown.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import poseidonscrown.PoseidonsCrownMod;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
	@Inject(method = "tick", at = @At("TAIL"))
	private void poseidons_crown$applyOceanEffects(CallbackInfo ci) {
		LivingEntity self = (LivingEntity) (Object) this;
		if (self.level().isClientSide()) {
			return;
		}
		ItemStack head = self.getItemBySlot(EquipmentSlot.HEAD);
		if (head.getItem() != PoseidonsCrownMod.POSEIDONS_CROWN_ITEM) {
			return;
		}
		int reapply = 400;
		self.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, reapply, 0, false, true, true));
		self.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, reapply, 0, false, true, true));
		self.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, reapply, 0, false, true, true));
	}
}
