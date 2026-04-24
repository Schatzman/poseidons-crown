package poseidonscrown.item;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

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
}
