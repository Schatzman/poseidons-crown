package poseidonscrown.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import poseidonscrown.PoseidonsCrownMod;

public class PoseidonsCrownRecipe implements CraftingRecipe {
	private final ResourceLocation id;
	private final CraftingBookCategory category;

	public PoseidonsCrownRecipe(ResourceLocation id, CraftingBookCategory category) {
		this.id = id;
		this.category = category;
	}

	@Override
	public boolean matches(CraftingContainer inv, Level level) {
		List<Integer> slots = new ArrayList<>();
		for (int i = 0; i < 9; i++) {
			if (!inv.getItem(i).isEmpty()) {
				slots.add(i);
			}
		}
		if (slots.size() != 3) {
			return false;
		}
		int a = slots.get(0);
		int b = slots.get(1);
		int c = slots.get(2);
		if (a / 3 != b / 3 || b / 3 != c / 3) {
			return false;
		}
		if (b != a + 1 || c != b + 1) {
			return false;
		}
		ItemStack helm = inv.getItem(a);
		ItemStack diamond = inv.getItem(b);
		ItemStack heart = inv.getItem(c);
		return helm.getItem() == Items.GOLDEN_HELMET
				&& !EnchantmentHelper.getEnchantments(helm).isEmpty()
				&& diamond.getItem() == Items.DIAMOND
				&& heart.getItem() == Items.HEART_OF_THE_SEA;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess reg) {
		return makeResult();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess reg) {
		return makeResult();
	}

	private static ItemStack makeResult() {
		ItemStack out = new ItemStack(PoseidonsCrownMod.POSEIDONS_CROWN_ITEM);
		out.enchant(Enchantments.AQUA_AFFINITY, 1);
		return out;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PoseidonsCrownMod.POSEIDONS_CROWN_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeType.CRAFTING;
	}

	@Override
	public CraftingBookCategory category() {
		return this.category;
	}
}
