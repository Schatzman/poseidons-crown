package poseidonscrown.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PoseidonsCrownItem extends ArmorItem {
	public PoseidonsCrownItem() {
		super(
				ModArmorMaterials.POSEIDONS_CROWN,
				ArmorItem.Type.HELMET,
				new Item.Properties().rarity(Rarity.EPIC));
	}

	@Override
	public void appendHoverText(
			ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(
				Component.translatable("item.poseidons_crown.poseidons_crown.lore.1")
						.withStyle(ChatFormatting.GRAY));
		tooltip.add(
				Component.translatable("item.poseidons_crown.poseidons_crown.lore.2")
						.withStyle(ChatFormatting.GRAY));
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public boolean canBeDepleted() {
		return false;
	}

	/**
	 * 1.20.1: Unbreakable NBT (same as {@code /give ... {Unbreakable:1}}) so durability
	 * is blocked, tooltip shows "Unbreakable", and other mods that read the tag behave.
	 */
	@Override
	public ItemStack getDefaultInstance() {
		ItemStack s = super.getDefaultInstance();
		s.getOrCreateTag().putBoolean("Unbreakable", true);
		return s;
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, level, entity, slot, selected);
		if (stack.getItem() != this) {
			return;
		}
		if (!stack.getOrCreateTag().getBoolean("Unbreakable")) {
			stack.getOrCreateTag().putBoolean("Unbreakable", true);
		}
		if (stack.getDamageValue() > 0) {
			stack.setDamageValue(0);
		}
	}
}
