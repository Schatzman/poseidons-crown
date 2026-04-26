package poseidonscrown.mixin;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import poseidonscrown.PoseidonsCrownMod;

@Mixin(PiglinAi.class)
public abstract class PiglinAiMixin {
	@Inject(method = "isWearingGold", at = @At("RETURN"), cancellable = true)
	private static void poseidons_crown$crownAsGold(
			LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ()) {
			return;
		}
		ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (head.getItem() == PoseidonsCrownMod.POSEIDONS_CROWN_ITEM) {
			cir.setReturnValue(true);
		}
	}
}
