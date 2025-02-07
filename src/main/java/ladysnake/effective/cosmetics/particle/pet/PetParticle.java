package ladysnake.effective.cosmetics.particle.pet;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Random;

public class PetParticle extends SpriteBillboardParticle {
	private static final Random RANDOM = new Random();
	protected final PlayerEntity owner;
	private final SpriteProvider spriteProvider;
	protected float alpha = 0f;

	public PetParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
		super(world, x, y, z);
		this.spriteProvider = spriteProvider;
		this.setSpriteForAge(spriteProvider);

		this.colorAlpha = 0;
		this.maxAge = 40;
		this.owner = world.getClosestPlayer(TargetPredicate.createNonAttackable().setBaseMaxDistance(1D), this.x, this.y, this.z);

		this.scale = 0.2f;

		if (this.owner == null) {
			this.markDead();
		}
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		Vec3d vec3d = camera.getPos();
		float f = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - vec3d.getX());
		float g = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - vec3d.getY());
		float h = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - vec3d.getZ());
		Quaternion quaternion2;
		if (this.angle == 0.0F) {
			quaternion2 = camera.getRotation();
		} else {
			quaternion2 = new Quaternion(camera.getRotation());
			float i = MathHelper.lerp(tickDelta, this.prevAngle, this.angle);
			quaternion2.hamiltonProduct(Vec3f.POSITIVE_Z.getRadialQuaternion(i));
		}

		Vec3f Vec3f = new Vec3f(-1.0F, -1.0F, 0.0F);
		Vec3f.rotate(quaternion2);
		Vec3f[] Vec3fs = new Vec3f[]{new Vec3f(-1.0F, -1.0F, 0.0F), new Vec3f(-1.0F, 1.0F, 0.0F), new Vec3f(1.0F, 1.0F, 0.0F), new Vec3f(1.0F, -1.0F, 0.0F)};
		float j = this.getSize(tickDelta);

		for (int k = 0; k < 4; ++k) {
			Vec3f Vec3f2 = Vec3fs[k];
			Vec3f2.rotate(quaternion2);
			Vec3f2.scale(j);
			Vec3f2.add(f, g, h);
		}

		float minU = this.getMinU();
		float maxU = this.getMaxU();
		float minV = this.getMinV();
		float maxV = this.getMaxV();
		int l = 15728880;

		vertexConsumer.vertex(Vec3fs[0].getX(), Vec3fs[0].getY(), Vec3fs[0].getZ()).uv(maxU, maxV).color(1f, 1f, 1f, alpha).light(l).next();
		vertexConsumer.vertex(Vec3fs[1].getX(), Vec3fs[1].getY(), Vec3fs[1].getZ()).uv(maxU, minV).color(1f, 1f, 1f, alpha).light(l).next();
		vertexConsumer.vertex(Vec3fs[2].getX(), Vec3fs[2].getY(), Vec3fs[2].getZ()).uv(minU, minV).color(1f, 1f, 1f, alpha).light(l).next();
		vertexConsumer.vertex(Vec3fs[3].getX(), Vec3fs[3].getY(), Vec3fs[3].getZ()).uv(minU, maxV).color(1f, 1f, 1f, alpha).light(l).next();
	}

	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		if (this.age > 10) {
			alpha = 1;
		} else {
			alpha = 0;
		}

		if (owner != null) {
			this.prevPosX = this.x;
			this.prevPosY = this.y;
			this.prevPosZ = this.z;

			// die if old enough
			if (this.age++ >= this.maxAge) {
				this.markDead();
			}

			this.setPos(owner.getX() + Math.cos(owner.bodyYaw / 50) * 0.5, owner.getY() + owner.getHeight() + 0.5f + Math.sin(owner.age / 12f) / 12f, owner.getZ() - Math.cos(owner.bodyYaw / 50) * 0.5);
		} else {
			this.markDead();
		}
	}


	public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
		private final SpriteProvider spriteProvider;

		public DefaultFactory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
			return new PetParticle(clientWorld, d, e, f, this.spriteProvider);
		}
	}

}
