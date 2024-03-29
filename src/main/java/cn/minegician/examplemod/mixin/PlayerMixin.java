package cn.minegician.examplemod.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author xcreeperz
 * @date 2024/3/29
 * @description
 */
@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci){
        ((Player)(Object)this).sendSystemMessage(Component.literal("test message from mixin."));
    }
}
