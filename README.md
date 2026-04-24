# Poseidon's Crown (Fabric 1.20.1)

- **Helmet** armor: 6 (double a netherite helmet's head defense: 3 x 2)
- Unbreakable; **Aqua Affinity** on the item; while worn: **Water Breathing**, **Dolphin's Grace**, **Conduit Power** (potion effect)

## Crafting (crafting table, one row, left to right)

1. Any **enchanted** golden helmet
2. **Diamond**
3. **Heart of the sea**

## Requirements

- Minecraft **1.20.1**
- Fabric Loader **≥ 0.18.4** (see `gradle.properties` / `fabric.mod.json`)
- [Fabric API](https://github.com/FabricMC/fabric) — **required** at runtime (`modImplementation` in `build.gradle`)

## Dev

- **JDK 17+**
- `./gradlew build` — JAR: `build/libs/poseidons-crown-<version>.jar` (put in the `mods` folder)
- `./gradlew runClient` for a local Loom game instance

## Textures and art

**Shipped assets**

- Worn model: `assets/poseidons_crown/textures/models/armor/poseidons_crown_layer_1.png` and `_layer_2.png` (name matches `ArmorMaterial.getName()`: `poseidons_crown`).
- Inventory icon: `assets/poseidons_crown/textures/item/poseidons_crown.png` (see `models/item/poseidons_crown.json`).

**Regenerating the composite (gold base + reference gems)**  
From the mod repo, after a successful `./gradlew build` (so the Loom merged jar exists):

```bash
python3 scripts/compose_poseidons_textures.py
```

The script loads vanilla `gold_layer_1` / `gold_layer_2` from the Gradle cache, composites `art/reference/diamond.png` and `heart_of_the_sea.png` onto the helmet UVs, and rebuilds the 16x16 item icon from `art/reference/golden_helmet.png`. Requires [Pillow](https://pillow.readthedocs.io/) (`pip install pillow`).

**Reference-only PNGs (palette / manual edits)**  
`art/reference/*.png` — extracted from 1.20.1 for look development.

**Optional: Blockbench**  
For UV preview or a fully custom item/block model, use [Blockbench](https://www.blockbench.net/) (desktop). This mod uses vanilla `ArmorItem` + flat armor layers; 2D edits in Aseprite/GIMP are enough for most changes.

## License / Mojang art

The compositor uses Mojang’s gold armor layers and item art as a **base** for local development; for public distribution, replace with fully original art or adjust per Mojang’s brand/modding terms as you see fit.
