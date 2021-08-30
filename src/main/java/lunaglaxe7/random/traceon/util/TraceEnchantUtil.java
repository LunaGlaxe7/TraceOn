package lunaglaxe7.random.traceon.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;


public class TraceEnchantUtil {
    public static void removeEnchant(@NotNull Enchantment enchantment,@NotNull ItemStack itemStack){

        NbtList enchantments = itemStack.getEnchantments();
        String id = String.valueOf(Registry.ENCHANTMENT.getId(enchantment));


        if (enchantments.size() == 1){
            itemStack.getNbt().remove("Enchantments");
        }else {
            for (int i = 0; i < enchantments.size(); i++) {
                NbtCompound nbtCompound = enchantments.getCompound(i);
                if (nbtCompound.get("id").asString().equals(id)) {
                    enchantments.remove(i);
                    break;
                }
            }

            if (enchantments.size() == 0) {
                itemStack.getNbt().remove("Enchantments");
            }
        }

        if (enchantment == Enchantments.MENDING){
            removeEnchantmentMendingHelp(itemStack);
        }
    }

    public static void removeEnchantmentMendingHelp(@NotNull ItemStack itemStack){
        itemStack.getNbt().remove("RepairCost");
    }
}
