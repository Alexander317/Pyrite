package cc.cassian.pyrite.registry.neoforge;

import cc.cassian.pyrite.blocks.*;
import cc.cassian.pyrite.functions.ModHelpers;
import cc.cassian.pyrite.functions.ModLists;
import cc.cassian.pyrite.registry.BlockCreator;
import cc.cassian.pyrite.registry.PyriteItemGroups;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static cc.cassian.pyrite.Pyrite.MOD_ID;
import static cc.cassian.pyrite.functions.ModHelpers.*;
import static cc.cassian.pyrite.functions.neoforge.NeoHelpers.*;
import static cc.cassian.pyrite.registry.PyriteItemGroups.POTTED_FLOWERS;

@SuppressWarnings("unused")
public class BlockCreatorImpl {
    // Creative tab icon holders
    public static Supplier<Block> WOOD_ICON;
    public static Supplier<Block> RESOURCE_ICON;
    public static Supplier<Block> BRICK_ICON;
    public static Supplier<Block> MISC_ICON;
    //Deferred registry entries
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(RegistryKeys.BLOCK, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(RegistryKeys.ITEM, MOD_ID);
    public static final DeferredRegister<ItemGroup> TABS = DeferredRegister.create(RegistryKeys.ITEM_GROUP, MOD_ID);
    public static final ArrayList<Supplier<Item>> ALL_ITEMS = new ArrayList<>();
    public static final ArrayList<Supplier<Block>> SIGN_BLOCKS = new ArrayList<>();
    public static final ArrayList<Supplier<Block>> HANGING_SIGN_BLOCKS = new ArrayList<>();
    // Sublists for Item Groups
    public static final ArrayList<Supplier<?>> WOOD_BLOCKS = new ArrayList<>();
    public static final ArrayList<Supplier<?>> RESOURCE_BLOCKS = new ArrayList<>();
    public static final ArrayList<Supplier<?>> BRICK_BLOCKS = new ArrayList<>();
    public static final ArrayList<Supplier<Block>> REDSTONE_BLOCKS = new ArrayList<>();
    public static final ArrayList<Supplier<?>> MISC_BLOCKS = new ArrayList<>();

    /**
     * Implements {@link BlockCreator#createWoodType(String, BlockSetType)} on NeoForge.
     */
    public static WoodType createWoodType(String blockID, BlockSetType setType) {
        WoodType woodType = new WoodType(locate(blockID).toString(), setType);
        WoodType.register(woodType);
        return woodType;
    }

    /**
     * Implements {@link BlockCreator#platformRegister(String, String, AbstractBlock.Settings, WoodType, BlockSetType, ParticleEffect, Block, String)} on NeoForge.
     */
    public static void platformRegister(String blockID, String blockType, AbstractBlock.Settings blockSettings, WoodType woodType, BlockSetType blockSetType, ParticleEffect particle, Block copyBlock, String group) {
        int power;
        if (blockID.contains("redstone")) power = 15;
        else power = 0;
        Supplier<Block> newBlock;
        switch (blockType.toLowerCase()) {
            case "block", "lamp":
                if (isCopper(blockID)) {
                    newBlock = BLOCKS.register(blockID, () -> new OxidizableBlock(ModHelpers.getOxidizationState(blockID), blockSettings));
                    registerBlock("waxed_"+blockID, () -> new ModBlock(blockSettings), "waxed_"+group);
                } else {
                    newBlock = BLOCKS.register(blockID, () -> new ModBlock(blockSettings, power));
                    if (power == 15) {
                        if (blockID.equals("lit_redstone_lamp"))
                            REDSTONE_BLOCKS.addFirst(newBlock);
                        else
                            REDSTONE_BLOCKS.add(newBlock);
                    }
                    if (Objects.equals(copyBlock, Blocks.OAK_PLANKS))
                        WOOD_BLOCKS.add(newBlock);
                }
                break;
            case "crafting":
                AbstractBlock.Settings craftingSettings;
                if (!(blockID.contains("crimson") || blockID.contains("warped"))) {
                    craftingSettings = blockSettings.burnable();
                }
                else craftingSettings = blockSettings;
                newBlock = BLOCKS.register(blockID, () -> new ModCraftingTable(craftingSettings));
                if (!(blockID.contains("crimson") || blockID.contains("warped"))) {
                    FUEL_BLOCKS.put(newBlock, 300);
                }
                WOOD_BLOCKS.add(newBlock);
                break;
            case "ladder":
                newBlock = BLOCKS.register(blockID, () -> new LadderBlock(blockSettings));
                WOOD_BLOCKS.add(newBlock);
                break;
            case "carpet":
                newBlock = BLOCKS.register(blockID, () -> new ModCarpet(blockSettings));
                break;
            case "slab":
                if (isCopper(blockID)) {
                    newBlock = BLOCKS.register(blockID, () -> new OxidizableSlabBlock(ModHelpers.getOxidizationState(blockID), blockSettings));
                    registerBlock("waxed_"+blockID, () -> new ModSlab(blockSettings), "waxed_"+group);
                } else {
                    newBlock = BLOCKS.register(blockID, () -> new ModSlab(blockSettings, power));
                }
                if (Objects.equals(copyBlock, Blocks.OAK_SLAB))
                    WOOD_BLOCKS.add(newBlock);
                if (power == 15)
                    REDSTONE_BLOCKS.add(newBlock);
                break;
            case "stairs":
                if (isCopper(blockID)) {
					newBlock = BLOCKS.register(blockID, () -> new OxidizableStairsBlock(ModHelpers.getOxidizationState(blockID), copyBlock.getDefaultState(), blockSettings));
                    registerBlock("waxed_"+blockID, () -> new ModStairs(copyBlock.getDefaultState(), blockSettings), "waxed_"+group);
                } else
                    newBlock = BLOCKS.register(blockID, () -> new ModStairs(copyBlock.getDefaultState(), blockSettings));
                if (Objects.equals(copyBlock, Blocks.OAK_STAIRS))
                    WOOD_BLOCKS.add(newBlock);
                break;
            case "wall":
                newBlock = BLOCKS.register(blockID, () -> new ModWall(blockSettings, power));
                if (power == 15)
                    REDSTONE_BLOCKS.add(newBlock);
                break;
            case "fence":
                newBlock = BLOCKS.register(blockID, () -> new FenceBlock(blockSettings));
                if (power == 15)
                    REDSTONE_BLOCKS.add(newBlock);
                break;
            case "log":
                if (isCopper(blockID)) {
                    newBlock = BLOCKS.register(blockID, () -> new OxidizablePillarBlock(ModHelpers.getOxidizationState(blockID), blockSettings));
                    registerBlock("waxed_"+blockID, () -> new ModPillar(blockSettings), "waxed_"+group);
                } else {
                    newBlock = BLOCKS.register(blockID, () -> new ModPillar(blockSettings, power));
                    if (blockID.contains("mushroom") || blockID.contains("log"))
                        WOOD_BLOCKS.add(newBlock);
                    else if (power == 15)
                        REDSTONE_BLOCKS.add(newBlock);
                }
                break;
            case "wood":
                newBlock = BLOCKS.register(blockID, () -> new ModWood(blockSettings));
                WOOD_BLOCKS.add(newBlock);
                break;
            case "facing":
                newBlock = BLOCKS.register(blockID, () -> new ModFacingBlock(blockSettings, power));
                if (power == 15)
                    REDSTONE_BLOCKS.add(newBlock);
                break;
            case "bars", "glass_pane", "tinted_glass_pane":
                newBlock = BLOCKS.register(blockID, () -> new ModPane(blockSettings, power));
                if (power == 15)
                    REDSTONE_BLOCKS.add(newBlock);
                break;
            case "stained_framed_glass_pane":
                newBlock = BLOCKS.register(blockID, () -> new StainedGlassPaneBlock(getDyeColorFromFramedId(blockID), blockSettings));
                break;
            case "glass", "tinted_glass":
                newBlock = BLOCKS.register(blockID, () -> new ModGlass(blockSettings));
                break;
            case "stained_framed_glass":
                newBlock = BLOCKS.register(blockID, () -> new StainedFramedGlass(ModHelpers.getDyeColorFromFramedId(blockID), blockSettings));
                break;
            case "gravel":
                newBlock = BLOCKS.register(blockID, () -> new GravelBlock(blockSettings));
                break;
            case "flower":
                newBlock = BLOCKS.register(blockID, () -> new FlowerBlock(StatusEffects.NIGHT_VISION, 5, blockSettings));
                Supplier<FlowerPotBlock> pot = BLOCKS.register("potted_"+blockID, () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, newBlock, AbstractBlock.Settings.create().breakInstantly().nonOpaque().pistonBehavior(PistonBehavior.DESTROY)));
                POTTED_FLOWERS.put(blockID, pot);
                break;
            case "fence_gate":
                newBlock = BLOCKS.register(blockID, () -> new FenceGateBlock(woodType, blockSettings));
                if (blockID.contains("_stained") || blockID.contains("mushroom"))
                    WOOD_BLOCKS.add(newBlock);
                break;
            case "wall_gate":
                newBlock = BLOCKS.register(blockID, () -> new WallGateBlock(blockSettings));
                break;
            case "sign":
                newBlock = BLOCKS.register(blockID, () -> new SignBlock(woodType, blockSettings));
                Supplier<Block> wallSign = BLOCKS.register(blockID.replace("_sign", "_wall_sign"), () -> new WallSignBlock(woodType, blockSettings));
                SIGN_BLOCKS.add(newBlock);
                SIGN_BLOCKS.add(wallSign);
                registerSignItem(newBlock, wallSign, blockID);
                break;
            case "hanging_sign":
                newBlock = BLOCKS.register(blockID, () -> new HangingSignBlock(woodType, blockSettings));
                Supplier<Block> hangingWallSign = BLOCKS.register(blockID.replace("_sign", "_wall_sign"), () -> new WallHangingSignBlock(woodType, blockSettings));
                HANGING_SIGN_BLOCKS.add(newBlock);
                HANGING_SIGN_BLOCKS.add(hangingWallSign);
                registerHangingSignItem(newBlock, hangingWallSign, blockID);
                break;
            case "door":
                newBlock = BLOCKS.register(blockID, () -> new DoorBlock(blockSetType, blockSettings.nonOpaque()));
                if (blockID.contains("_stained") || blockID.contains("mushroom"))
                    WOOD_BLOCKS.add(newBlock);
                break;
            case "trapdoor":
                newBlock = BLOCKS.register(blockID, () -> new TrapdoorBlock(blockSetType, blockSettings.nonOpaque()));
                if (blockID.contains("_stained") || blockID.contains("mushroom"))
                    WOOD_BLOCKS.add(newBlock);
                break;
            case "button":
                newBlock = BLOCKS.register(blockID, () -> new ModWoodenButton(blockSettings, blockSetType));
                if (blockID.contains("_stained") || blockID.contains("mushroom"))
                    WOOD_BLOCKS.add(newBlock);
                break;
            case "pressure_plate":
                newBlock = BLOCKS.register(blockID, () -> new ModPressurePlate(blockSettings, blockSetType));
                if (blockID.contains("_stained") || blockID.contains("mushroom"))
                    WOOD_BLOCKS.add(newBlock);
                break;
            case "torch":
                if (particle == null)
                    newBlock = BLOCKS.register(blockID, () -> new ModTorch(blockSettings.nonOpaque(), ParticleTypes.FLAME));
                else
                    newBlock = BLOCKS.register(blockID, () -> new ModTorch(blockSettings.nonOpaque(), particle));
                break;
            case "torch_lever":
                newBlock = BLOCKS.register(blockID, () -> new TorchLever(blockSettings.nonOpaque(), particle));
                REDSTONE_BLOCKS.add(newBlock);
                break;
            case "concrete_powder":
                newBlock = BLOCKS.register(blockID, () -> new ConcretePowderBlock((ModHelpers.getBlock(blockID.replace("_powder", ""))), blockSettings));
                break;
            case "switchable_glass":
                newBlock = BLOCKS.register(blockID, () -> new SwitchableGlass(blockSettings));
                REDSTONE_BLOCKS.add(newBlock);
                break;
            default:
                log("%s created as a generic block, block provided: %s".formatted(blockID, blockType));
                newBlock = BLOCKS.register(blockID, () -> new Block(blockSettings));
                break;
        }
        for (Block block : ModLists.getVanillaResourceBlocks()) {
            if (blockID.contains(Registries.BLOCK.getId(block).getPath().replace("_block", "")) && !inGroup(newBlock))
                RESOURCE_BLOCKS.add(newBlock);
        }
        if (blockID.contains("brick") && !inGroup(newBlock))
            BRICK_BLOCKS.add(newBlock);
        if (blockID.equals("dragon_stained_crafting_table")) WOOD_ICON = newBlock;
        else if (blockID.equals("cut_emerald")) RESOURCE_ICON = newBlock;
        else if (blockID.equals("cobblestone_bricks")) BRICK_ICON = newBlock;
        else if (blockID.equals("glowing_obsidian")) MISC_ICON = newBlock;
        else if (blockID.contains("grass")) addGrassBlock(newBlock);
        if (!blockType.equals("sign") && !blockType.equals("hanging_sign")) {
            registerBlockItem(blockID, newBlock);
            if (!inGroup(newBlock)) {
                MISC_BLOCKS.add(newBlock);
            }
        }
        PyriteItemGroups.match(newBlock, copyBlock, group, blockID);

    }

    public static void registerBlock(String blockID, Supplier<Block> block, String group) {
        var newBlock = BLOCKS.register(blockID, block);
        registerBlockItem(blockID, newBlock);
        PyriteItemGroups.match(newBlock, null, group, blockID);
    }

    public static void registerBlockItem(String blockID, Supplier<Block> newBlock) {
        Item.Settings settings = new Item.Settings();
        if (blockID.contains("netherite"))
            settings = settings.fireproof();
        final Item.Settings finalSettings = settings;
        ALL_ITEMS.add(ITEMS.register(blockID, () -> new BlockItem(newBlock.get(), finalSettings)));
    }

    public static void registerSignItem(Supplier<Block> newBlock, Supplier<Block> wallSign, String blockID) {
        Supplier<Item> newItem = ITEMS.register(blockID, () -> new SignItem(new Item.Settings().maxCount(16), newBlock.get(), wallSign.get()));
        ALL_ITEMS.add(newItem);
        WOOD_BLOCKS.add(newItem);
    }

    public static void registerHangingSignItem(Supplier<Block> newBlock, Supplier<Block> wallSign, String blockID) {
        Supplier<Item> newItem = ITEMS.register(blockID, () -> new HangingSignItem(newBlock.get(), wallSign.get(), new Item.Settings().maxCount(16)));
        ALL_ITEMS.add(newItem);
        WOOD_BLOCKS.add(newItem);
    }

    public static boolean inGroup(Object obj) {
        return WOOD_BLOCKS.contains(obj) || BRICK_BLOCKS.contains(obj) || RESOURCE_BLOCKS.contains(obj) || REDSTONE_BLOCKS.contains(obj) || MISC_BLOCKS.contains(obj);
    }

    public static void addItemGroup(String id, Supplier<Block> icon, ArrayList<Supplier<?>> blocks) {
        Supplier<ItemGroup> PYRITE_GROUP = TABS.register(id, () -> ItemGroup.builder()
            //Set the title of the tab.
            .displayName(Text.translatable("itemGroup." + MOD_ID + "." + id))
            //Set the icon of the tab.
            .icon(() -> new ItemStack(icon.get()))
            //Add your items to the tab.
            .entries((params, entries) -> {
                for (Supplier<?> obj : blocks) {
                    if (obj.get() instanceof Block block)
                        entries.add(block);
                    else if (obj.get() instanceof Item item)
                        entries.add(item);
                }
            })
            .build()
    );
    }

    /**
     * Implements {@link BlockCreator#registerPyriteItem(String)} on NeoForge.
     * This registers a basic item with no additional settings - primarily used for Dye.
     */
    public static void registerPyriteItem(String itemID) {
        ALL_ITEMS.add(ITEMS.register(itemID, () -> (new Item(new Item.Settings()))));
    }

    public static void register(IEventBus eventBus) {
        // TODO - Add vanilla Concrete to Pyrite item group.
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TABS.register(eventBus);
        // Add block groups to legacy item groups
        RESOURCE_BLOCKS.addAll(PyriteItemGroups.WAXED_COPPER_BLOCKS.values());
        RESOURCE_BLOCKS.addAll(PyriteItemGroups.WAXED_EXPOSED_COPPER_BLOCKS.values());
        RESOURCE_BLOCKS.addAll(PyriteItemGroups.WAXED_WEATHERED_COPPER_BLOCKS.values());
        RESOURCE_BLOCKS.addAll(PyriteItemGroups.WAXED_OXIDIZED_COPPER_BLOCKS.values());
        // Add item groups
        addItemGroup("wood_group", WOOD_ICON, WOOD_BLOCKS);
        addItemGroup("resource_group", RESOURCE_ICON, RESOURCE_BLOCKS);
        addItemGroup("brick_group", BRICK_ICON, BRICK_BLOCKS);
        addItemGroup("pyrite_group", MISC_ICON, MISC_BLOCKS);
    }

    // Adds Pyrite's signs to the list of blockstates that the Sign block entities support.
    @SubscribeEvent
    public static void addSignsToSupports(BlockEntityTypeAddBlocksEvent event) {
        for (Supplier<Block> sign : SIGN_BLOCKS) {
            event.modify(BlockEntityType.SIGN, sign.get());
        }
        for (Supplier<Block> sign : HANGING_SIGN_BLOCKS) {
            event.modify(BlockEntityType.HANGING_SIGN, sign.get());
        }
    }

    // Adds Pyrite's flowers to the flower pot block.
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        for (Map.Entry<String, Supplier<FlowerPotBlock>> entry : POTTED_FLOWERS.entrySet()) {
            String flowerID = entry.getKey();
            Supplier<FlowerPotBlock> flowerPot = entry.getValue();
            pot.addPlant(ModHelpers.locate(flowerID), flowerPot);
        }
    }
}
