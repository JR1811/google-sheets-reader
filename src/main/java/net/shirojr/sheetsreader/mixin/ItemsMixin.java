package net.shirojr.sheetsreader.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.shirojr.sheetsreader.SheetsReader;
import net.shirojr.sheetsreader.sheet.SheetsElement;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemsMixin {
    @Inject(method = "appendTooltip", at = @At("HEAD"))
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        if (world == null) return;

        for (SheetsElement entry : SheetsReader.getElementList()) {
            Identifier regId = Registry.ITEM.getId(stack.getItem());
            if (regId.equals(entry.id())) {
                if (entry.restriction() != null) {
                    Style restrictionStyle = Style.EMPTY;
                    switch (entry.restriction()) {
                        case "Banned" -> restrictionStyle = restrictionStyle.withBold(true).withColor(Formatting.RED);
                        case "Partially Usable" ->
                                restrictionStyle = restrictionStyle.withBold(true).withColor(Formatting.GOLD);
                        default -> restrictionStyle = restrictionStyle.withBold(true).withColor(Formatting.GREEN);
                    }
                    var restrictionText = new LiteralText(entry.restriction()).setStyle(restrictionStyle);
                    tooltip.add(restrictionText);
                }

                if (Screen.hasShiftDown()) {
                    Style reasonStyle = Style.EMPTY.withColor(Formatting.GRAY);
                    var reasonText = new LiteralText(entry.reason()).setStyle(reasonStyle);
                    tooltip.add(reasonText);
                }
            }
        }
    }
}
