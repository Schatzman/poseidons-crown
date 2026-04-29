#!/usr/bin/env python3
"""Compose Poseidon's Crown textures from stable references.

The compositor is idempotent: outputs are rebuilt from vanilla/reference inputs rather than
composited over existing target PNGs. Use --dry-run for GN checks that must leave repos/* clean.
"""
from __future__ import annotations

import argparse
import tempfile
import zipfile
from io import BytesIO
from pathlib import Path

from PIL import Image

HERE = Path(__file__).resolve().parent
REPO = HERE.parent
REF = REPO / "art" / "reference"
OUT_ARMOR = REPO / "src/main/resources/assets/poseidons_crown/textures/models/armor"
OUT_ITEM = REPO / "src/main/resources/assets/poseidons_crown/textures/item"
MC_ARMOR = REPO / "src/main/resources/assets/minecraft/textures/models/armor"


def find_merged_jar(repo: Path) -> Path:
    base = repo / ".gradle" / "loom-cache" / "minecraftMaven" / "net" / "minecraft"
    if not base.is_dir():
        raise SystemExit(f"Missing {base} (run ./gradlew build in mod repo first)")
    for p in sorted(base.rglob("minecraft-merged-*1.20.1*.jar")):
        if p.name.endswith("-sources.jar"):
            continue
        return p
    raise SystemExit("No minecraft-merged 1.20.1 jar; run ./gradlew build first")


def load_from_jar(z: zipfile.ZipFile, path: str) -> Image.Image:
    return Image.open(BytesIO(z.read(path))).convert("RGBA")


def load_ref(name: str) -> Image.Image:
    return Image.open(BytesIO(REF.joinpath(name).read_bytes())).convert("RGBA")


def resize_nearest(src: Image.Image, w: int, h: int) -> Image.Image:
    return src.resize((w, h), Image.Resampling.NEAREST)


def compose(out_armor: Path, out_item: Path, *, mirror_minecraft: bool) -> list[Path]:
    jar = find_merged_jar(REPO)
    with zipfile.ZipFile(jar) as z:
        gold1 = load_from_jar(z, "assets/minecraft/textures/models/armor/gold_layer_1.png")
        gold2 = load_from_jar(z, "assets/minecraft/textures/models/armor/gold_layer_2.png")

    dim = load_ref("diamond.png")
    heart = load_ref("heart_of_the_sea.png")
    helm_item = load_ref("golden_helmet.png")

    l1 = gold1.copy()
    l1.paste(resize_nearest(dim, 6, 6), (9, 1), resize_nearest(dim, 6, 6))
    l1.paste(resize_nearest(heart, 6, 6), (25, 1), resize_nearest(heart, 6, 6))
    l1.paste(resize_nearest(dim, 4, 4), (2, 2), resize_nearest(dim, 4, 4))

    l2 = gold2.copy()
    item = helm_item.copy()
    item.paste(resize_nearest(dim, 5, 5), (10, 2), resize_nearest(dim, 5, 5))
    item.paste(resize_nearest(heart, 5, 5), (2, 2), resize_nearest(heart, 5, 5))

    out_armor.mkdir(parents=True, exist_ok=True)
    out_item.mkdir(parents=True, exist_ok=True)
    written = [
        out_armor / "poseidons_crown_layer_1.png",
        out_armor / "poseidons_crown_layer_2.png",
        out_item / "poseidons_crown.png",
    ]
    l1.save(written[0])
    l2.save(written[1])
    item.save(written[2])
    if mirror_minecraft:
        MC_ARMOR.mkdir(parents=True, exist_ok=True)
        mirror = MC_ARMOR / "poseidons_crown_layer_1.png"
        l1.save(mirror)
        written.append(mirror)
    return written


def check_outputs(paths: list[Path]) -> None:
    expected = {
        "poseidons_crown.png": (16, 16),
        "poseidons_crown_layer_1.png": (64, 32),
        "poseidons_crown_layer_2.png": (64, 32),
    }
    for path in paths:
        img = Image.open(path).convert("RGBA")
        want = expected[path.name]
        if img.size != want:
            raise SystemExit(f"dimension mismatch for {path}: got {img.size}, want {want}")
        if not any(px[3] for px in img.getdata()):
            raise SystemExit(f"all-transparent output: {path}")


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--dry-run", action="store_true", help="compose into a temporary directory and leave repo PNGs untouched")
    parser.add_argument("--check", action="store_true", help="verify generated PNG dimensions and non-empty alpha")
    parser.add_argument("--output-root", type=Path, help="write under OUTPUT_ROOT/{models/armor,item} instead of the mod resources")
    args = parser.parse_args()

    with tempfile.TemporaryDirectory(prefix="poseidon-compose-") as tmp:
        if args.dry_run:
            base = Path(tmp)
            out_armor = base / "models" / "armor"
            out_item = base / "item"
            mirror = False
        elif args.output_root is not None:
            out_armor = args.output_root / "models" / "armor"
            out_item = args.output_root / "item"
            mirror = False
        else:
            out_armor = OUT_ARMOR
            out_item = OUT_ITEM
            mirror = True
        paths = compose(out_armor, out_item, mirror_minecraft=mirror)
        if args.check or args.dry_run:
            check_outputs(paths)
        for path in paths:
            print("Wrote", path)
        if args.dry_run:
            print("dry-run: repository textures untouched")


if __name__ == "__main__":
    main()
