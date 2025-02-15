package ladysnake.effective.mixin.screenshake;

import com.sammy.lodestone.handlers.ScreenshakeHandler;
import com.sammy.lodestone.systems.rendering.particle.Easing;
import com.sammy.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import com.sammy.lodestone.systems.screenshake.ScreenshakeInstance;
import ladysnake.effective.EffectiveConfig;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.warden.WardenEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WardenEntity.class)
public class WardenRoarScreenshakeAdder extends HostileEntity {
	public ScreenshakeInstance roarScreenShake;
	public int ticksSinceAnimationStart = 0;

	protected WardenRoarScreenshakeAdder(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (EffectiveConfig.wardenScreenShake && this.getPose().equals(EntityPose.ROARING)) {
			ticksSinceAnimationStart++;
			if (roarScreenShake == null) {
				if (ticksSinceAnimationStart >= 20) {
					roarScreenShake = new PositionedScreenshakeInstance(70, this.getPos(), 20f, 0f, 25f, Easing.CIRC_IN_OUT).setIntensity(0.0f, 1.0f, 0.0f);
					ScreenshakeHandler.addScreenshake(roarScreenShake);
				}
			}
		} else {
			roarScreenShake = null;
			ticksSinceAnimationStart = 0;
		}
	}


}
