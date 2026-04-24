#!/usr/bin/env python3
"""One-shot compositor: gold armor L1 + item + gem reference PNGs -> mod textures."""
from __future__ import annotations

import zipfile
from io import BytesIO
from pathlib import Path

from PIL import Image

HERE = Path(__file__).resolve().parent
REPO = HERE.parent
REF = REPO / "art" / "reference"
OUT_ARMOR = REPO / "src/main/resources/assets/poseidons_crown/textures/models/armor"
OUT_ITEM = REPO / "src/main/resources/assets/poseidons_crown/textures/item"


def find_merged_jar(repo: Path) -> Path:
    base = repo / ".gradle" / "loom-cache" / "minecraftMaven" / "net" / "minecraft"
    if not base.is_dir():
        raise SystemExit(f"Missing {base} (run ./gradlew build in mod repo first)")
    for p in sorted(base.rglob("minecraft-merged-*1.20.1*.jar")):
        return p
    raise SystemExit("No minecraft-merged 1.20.1 jar; run ./gradlew build first")


def load_from_jar(z: zipfile.ZipFile, path: str) -> Image.Image:
    return Image.open(BytesIO(z.read(path))).convert("RGBA")


def resize_nearest(src: Image.Image, w: int, h: int) -> Image.Image:
    return src.resize((w, h), Image.Resampling.NEAREST)


def main() -> None:
    jar = find_merged_jar(REPO)
    with zipfile.ZipFile(jar) as z:
        gold1 = load_from_jar(z, "assets/minecraft/textures/models/armor/gold_layer_1.png")
        gold2 = load_from_jar(z, "assets/minecraft/textures/models/armor/gold_layer_2.png")

    dim = Image.open(BytesIO(REF.joinpath("diamond.png").read_bytes())).convert("RGBA")
    heart = Image.open(BytesIO(REF.joinpath("heart_of_the_sea.png").read_bytes())).convert("RGBA")
    helm_item = Image.open(BytesIO(REF.joinpath("golden_helmet.png").read_bytes())).convert("RGBA")

    l1 = gold1.copy()
    d6 = resize_nearest(dim, 6, 6)
    h6 = resize_nearest(heart, 6, 6)
    l1.paste(d6, (9, 1), d6)
    l1.paste(h6, (25, 1), h6)
    d4 = resize_nearest(dim, 4, 4)
    l1.paste(d4, (2, 2), d4)

    l2 = gold2.copy()

    OUT_ARMOR.mkdir(parents=True, exist_ok=True)
    l1.save(OUT_ARMOR / "poseidons_crown_layer_1.png")
    l2.save(OUT_ARMOR / "poseidons_crown_layer_2.png")

    item = helm_item.copy()
    d5 = resize_nearest(dim, 5, 5)
    h5 = resize_nearest(heart, 5, 5)
    item.paste(d5, (10, 2), d5)
    item.paste(h5, (2, 2), h5)
    OUT_ITEM.mkdir(parents=True, exist_ok=True)
    item.save(OUT_ITEM / "poseidons_crown.png")
    for p in (OUT_ARMOR / "poseidons_crown_layer_1.png", OUT_ARMOR / "poseidons_crown_layer_2.png", OUT_ITEM / "poseidons_crown.png"):
        print("Wrote", p)


if __name__ == "__main__":
    main()
