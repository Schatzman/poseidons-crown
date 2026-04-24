package poseidonscrown;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import poseidonscrown.item.PoseidonsCrownItem;
import poseidonscrown.recipe.PoseidonsCrownRecipe;

public final class PoseidonsCrownMod implements ModInitializer {
	public static final String MOD_ID = "poseidons_crown";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Item POSEIDONS_CROWN_ITEM = new PoseidonsCrownItem();
	public static final RecipeSerializer<PoseidonsCrownRecipe> POSEIDONS_CROWN_SERIALIZER =
			new SimpleCraftingRecipeSerializer<>(
					(id, category) -> new PoseidonsCrownRecipe(id, category));

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "poseidons_crown"), POSEIDONS_CROWN_ITEM);
		Registry.register(
				BuiltInRegistries.RECIPE_SERIALIZER,
				new ResourceLocation(MOD_ID, "poseidons_crown"),
				POSEIDONS_CROWN_SERIALIZER);
		registerCreativeTab();
		LOGGER.info("Poseidon's Crown initialized");
	}

	private static void registerCreativeTab() {
		CreativeModeTab tab = FabricItemGroup.builder()
				.title(Component.translatable("itemGroup.poseidons_crown.poseidons_crown"))
				.icon(() -> new ItemStack(POSEIDONS_CROWN_ITEM))
				.displayItems((parameters, output) -> {
					output.accept(POSEIDONS_CROWN_ITEM);
				})
				.build();
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(MOD_ID, "poseidons_crown"), tab);
	}
}
