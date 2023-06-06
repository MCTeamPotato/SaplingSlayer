package com.teampotato.saplingslayer;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;

import static com.teampotato.saplingslayer.SaplingSlayer.*;

@SuppressWarnings("NullableProblems")
public class SaplingSlayerEnchantment extends Enchantment {

    private static final EquipmentSlotType[] MAIN_HAND = new EquipmentSlotType[]{EquipmentSlotType.MAINHAND};
    private static final EnchantmentType ENCHANTMENT_TYPE = EnchantmentType.create(ID + ":on_shear", null);

    private static Rarity getRarityInConfig() {
        switch (rarity.get()) {
            case "COMMON":
                return Rarity.COMMON;
            case "UNCOMMON":
                return Rarity.UNCOMMON;
            case "RARE":
                return Rarity.RARE;
            case "VERY_RARE":
                return Rarity.VERY_RARE;
            default:
                LOGGER.error("Your rarity value in SapingSlayer config is invalid. Switch to COMMON rarity");
                return Rarity.COMMON;
        }
    }

    protected SaplingSlayerEnchantment() {
        super(getRarityInConfig(), ENCHANTMENT_TYPE, MAIN_HAND);
    }

    public boolean canEnchant(ItemStack pStack) {
        return canApplyAtEnchantingTable(pStack) && pStack.getItem() instanceof ShearsItem;
    }

    public boolean isTreasureOnly() {
        return isTreasureOnly.get();
    }

    public boolean isCurse() {
        return isCurse.get();
    }

    public boolean isTradeable() {
        return isTradeable.get();
    }

    public boolean isDiscoverable() {
        return isDiscoverable.get();
    }

    public boolean isAllowedOnBooks() {
        return isAllowedOnBooks.get();
    }
}