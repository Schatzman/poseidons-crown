package poseidonscrown.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class PoseidonsCrownItem extends ArmorItem {
	public PoseidonsCrownItem() {
		super(
				ModArmorMaterials.POSEIDONS_CROWN,
				ArmorItem.Type.HELMET,
				new Item.Properties().rarity(Rarity.EPIC));
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
		ItemStack stack = super.getDefaultInstance();
		stack.getOrCreateTag().putBoolean("Unbreakable", true);
		return stack;
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
