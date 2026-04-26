package poseidonscrown.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
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
		return helm.getItem() == Items.TURTLE_HELMET
				&& diamond.getItem() == Items.DIAMOND
				&& heart.getItem() == Items.HEART_OF_THE_SEA;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess reg) {
		ItemStack helm = ItemStack.EMPTY;
		for (int i = 0; i < 9; i++) {
			ItemStack s = inv.getItem(i);
			if (s.getItem() == Items.TURTLE_HELMET) {
				helm = s;
				break;
			}
		}
		return mergeHelmIntoCrown(helm);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess reg) {
		return makeResultPreview();
	}

	/** JEI/REI: preview without a specific turtle stack. */
	private static ItemStack makeResultPreview() {
		ItemStack out = new ItemStack(PoseidonsCrownMod.POSEIDONS_CROWN_ITEM);
		out.getOrCreateTag().putBoolean("Unbreakable", true);
		out.enchant(Enchantments.AQUA_AFFINITY, 1);
		return out;
	}

	/** Result: unbreakable + helm enchantments, Aqua Affinity at least 1. */
	public static ItemStack mergeHelmIntoCrown(ItemStack helm) {
		ItemStack out = new ItemStack(PoseidonsCrownMod.POSEIDONS_CROWN_ITEM);
		out.getOrCreateTag().putBoolean("Unbreakable", true);
		Map<Enchantment, Integer> fromHelm = EnchantmentHelper.getEnchantments(helm);
		HashMap<Enchantment, Integer> combined = new HashMap<>();
		if (!fromHelm.isEmpty()) {
			for (var e : fromHelm.entrySet()) {
				Enchantment k = e.getKey();
				if (k == null) {
					continue;
				}
				combined.put(k, e.getValue());
			}
		}
		int aa = Math.max(1, combined.getOrDefault(Enchantments.AQUA_AFFINITY, 0));
		combined.put(Enchantments.AQUA_AFFINITY, aa);
		EnchantmentHelper.setEnchantments(combined, out);
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
