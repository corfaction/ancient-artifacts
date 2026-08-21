package net.corfaction.ancientartifacts.mixin.anvil;

import net.corfaction.ancientartifacts.item.ModItems;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Inject(
            method = "createResult",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ancientArtifacts$repairGuardianTalisman(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;

        ItemStack input = menu.getSlot(0).getItem();
        ItemStack addition = menu.getSlot(1).getItem();

        if (!input.is(ModItems.GUARDIAN_TALISMAN)
                || !addition.is(ModItems.METAL_FRAGMENT)) {
            return;
        }

        ItemStack result = input.copy();

        int price = 0;
        int repairAmount = Math.min(
                result.getDamageValue(),
                result.getMaxDamage() / 4
        );

        if (repairAmount <= 0) {
            menu.getSlot(2).set(ItemStack.EMPTY);
            ((AnvilMenuAccessor) menu)
                    .ancientArtifacts$getCost()
                    .set(0);

            ci.cancel();
            return;
        }

        int count;

        for (
                count = 0;
                repairAmount > 0 && count < addition.getCount();
                count++
        ) {
            int resultDamage = result.getDamageValue() - repairAmount;

            result.setDamageValue(resultDamage);

            price += 5;

            repairAmount = Math.min(
                    result.getDamageValue(),
                    result.getMaxDamage() / 4
            );
        }

        ((AnvilMenuAccessor) menu)
                .ancientArtifacts$getCost()
                .set(price);

        menu.getSlot(2).set(result);

        ci.cancel();
    }
}