package com.teampotato.saplingslayer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@Mod(SaplingSlayer.ID)
@Mod.EventBusSubscriber(modid = SaplingSlayer.ID)
public class SaplingSlayer {
    public static final String ID = "saplingslayer";
    public static final Logger LOGGER = LogManager.getLogger("SapingSlayer");
    public static final IForgeRegistry<Item> ITEMS = ForgeRegistries.ITEMS;
    public static final DeferredRegister<Enchantment> ENCHANTMENT_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, ID);
    public static final RegistryObject<Enchantment> SAPLING_SLAYER = ENCHANTMENT_DEFERRED_REGISTER.register("sapling_slayer", SaplingSlayerEnchantment::new);
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    public SaplingSlayer() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, configSpec);
        ENCHANTMENT_DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        PlayerEntity player = event.getPlayer();
        ItemStack item = player.getMainHandItem();
        BlockPos pos = event.getPos();
        Block block = player.level.getBlockState(event.getPos()).getBlock();
        if (!item.getEnchantmentTags().toString().contains("sapling_slayer") || !(item.getItem() instanceof ShearsItem) || !(block instanceof LeavesBlock) || event.isCanceled()) return;
        if (item.isDamageableItem() && !player.level.isClientSide) item.setDamageValue((int) (item.getDamageValue() + item.getMaxDamage() * 0.1));
        dropSapling(player.level, pos, (LeavesBlock) block);
        player.level.setBlock(pos, AIR, 1);
    }

    private static void dropSapling(World world, BlockPos pos, LeavesBlock leaves) {
        List<Item> saplings = ItemTags.SAPLINGS.getValues();
        ResourceLocation registryName = leaves.getRegistryName();
        int sapling = saplings.indexOf(ITEMS.getValue(new ResourceLocation(Objects.requireNonNull(registryName).getNamespace(), registryName.getPath().replace("_leaves", "_sapling"))));
        if (sapling == -1) {
            LOGGER.error("SapingSlayer: Failed to find the corresponding sapling of the leaves");
            LOGGER.error(new ResourceLocation(Objects.requireNonNull(registryName).getNamespace(), registryName.getPath().replace("_leaves", "_sapling")));
            return;
        }
        world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), saplings.get(sapling).getDefaultInstance()));
    }

    public static ForgeConfigSpec configSpec;
    public static ForgeConfigSpec.BooleanValue isTradeable, isCurse, isTreasureOnly, isDiscoverable, isAllowedOnBooks;
    public static ForgeConfigSpec.ConfigValue<String> rarity;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Sapling Slayer Enchantment Attribute");
        isTradeable = builder.define("isTradeable", true);
        isCurse = builder.define("isCurse", false);
        isTreasureOnly = builder.define("isTreasure", false);
        isDiscoverable = builder.define("canBeFoundInLoot", true);
        isAllowedOnBooks = builder.define("isAllowedOnBooks", true);
        rarity = builder.comment("Allowed value: COMMON, UNCOMMON, RARE, VERY_RARE").define("rarity", "COMMON");
        builder.pop();
        configSpec = builder.build();
    }
}