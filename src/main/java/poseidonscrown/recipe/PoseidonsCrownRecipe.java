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

/**
 * Two patterns, one serializer: (1) one row: enchanted golden helmet + diamond + heart of the
 * sea; (2) shapeless: any golden helmet + any enchanted book (non-empty) + diamond + heart.
 */
public class PoseidonsCrownRecipe implements CraftingRecipe {
	private final ResourceLocation id;
	private final CraftingBookCategory category;

	public PoseidonsCrownRecipe(ResourceLocation id, CraftingBookCategory category) {
		this.id = id;
		this.category = category;
	}

	@Override
	public boolean matches(CraftingContainer inv, Level level) {
		return matchesShaped(inv) || matchesShapeless(inv);
	}

	/** One horizontal row: enchanted gold helm, diamond, heart. */
	private static boolean matchesShaped(CraftingContainer inv) {
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
		if (helm.getItem() != Items.GOLDEN_HELMET
				|| diamond.getItem() != Items.DIAMOND
				|| heart.getItem() != Items.HEART_OF_THE_SEA) {
			return false;
		}
		return !EnchantmentHelper.getEnchantments(helm).isEmpty();
	}

	/** Four items, any layout: gold helm, enchanted book, diamond, heart. */
	private static boolean matchesShapeless(CraftingContainer inv) {
		int gold = 0;
		int book = 0;
		int diamond = 0;
		int heart = 0;
		int nonEmpty = 0;
		for (int i = 0; i < 9; i++) {
			ItemStack s = inv.getItem(i);
			if (s.isEmpty()) {
				continue;
			}
			nonEmpty++;
			if (s.getItem() == Items.GOLDEN_HELMET) {
				gold++;
			} else if (s.getItem() == Items.ENCHANTED_BOOK) {
				book++;
			} else if (s.getItem() == Items.DIAMOND) {
				diamond++;
			} else if (s.getItem() == Items.HEART_OF_THE_SEA) {
				heart++;
			} else {
				return false;
			}
		}
		if (nonEmpty != 4) {
			return false;
		}
		if (gold != 1 || diamond != 1 || heart != 1 || book != 1) {
			return false;
		}
		for (int i = 0; i < 9; i++) {
			ItemStack s = inv.getItem(i);
			if (s.getItem() == Items.ENCHANTED_BOOK) {
				return !EnchantmentHelper.getEnchantments(s).isEmpty();
			}
		}
		return false;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess reg) {
		if (matchesShaped(inv)) {
			for (int i = 0; i < 9; i++) {
				ItemStack s = inv.getItem(i);
				if (s.getItem() == Items.GOLDEN_HELMET) {
					return mergeIntoCrown(s, ItemStack.EMPTY);
				}
			}
		}
		if (matchesShapeless(inv)) {
			ItemStack helm = ItemStack.EMPTY;
			ItemStack book = ItemStack.EMPTY;
			for (int i = 0; i < 9; i++) {
				ItemStack s = inv.getItem(i);
				if (s.getItem() == Items.GOLDEN_HELMET) {
					helm = s;
				} else if (s.getItem() == Items.ENCHANTED_BOOK) {
					book = s;
				}
			}
			return mergeIntoCrown(helm, book);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess reg) {
		return makeResultPreview();
	}

	private static ItemStack makeResultPreview() {
		ItemStack out = new ItemStack(PoseidonsCrownMod.POSEIDONS_CROWN_ITEM);
		out.getOrCreateTag().putBoolean("Unbreakable", true);
		out.enchant(Enchantments.AQUA_AFFINITY, 1);
		return out;
	}

	private static void addEnchantMergeInto(
			Map<Enchantment, Integer> into, Map<Enchantment, Integer> from) {
		for (var e : from.entrySet()) {
			Enchantment k = e.getKey();
			if (k == null) {
				continue;
			}
			into.merge(k, e.getValue(), Math::max);
		}
	}

	/** Merges helm and optional enchanted book, then enforces at least Aqua Affinity I. */
	public static ItemStack mergeIntoCrown(ItemStack helm, ItemStack book) {
		ItemStack out = new ItemStack(PoseidonsCrownMod.POSEIDONS_CROWN_ITEM);
		out.getOrCreateTag().putBoolean("Unbreakable", true);
		HashMap<Enchantment, Integer> combined = new HashMap<>();
		addEnchantMergeInto(combined, EnchantmentHelper.getEnchantments(helm));
		if (!book.isEmpty()) {
			addEnchantMergeInto(combined, EnchantmentHelper.getEnchantments(book));
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
