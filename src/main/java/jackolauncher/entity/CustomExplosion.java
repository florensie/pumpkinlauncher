package jackolauncher.entity;

import com.google.common.collect.Sets;
import jackolauncher.JackOLauncher;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class CustomExplosion extends Explosion {

    protected final boolean shouldCauseFire;
    protected final boolean shouldDamageTerrain;
    protected final boolean shouldDamageShooter;
    protected final Random random;
    protected final World world;
    protected final double x;
    protected final double y;
    protected final double z;
    protected final Entity exploder;
    protected final LivingEntity shootingEntity;
    protected final float explosionPower;
    protected final int extraDamage;
    protected final ItemStack tool = new ItemStack(JackOLauncher.JACK_O_LAUNCHER);

    public CustomExplosion(World world, Entity explodingEntity, LivingEntity shootingEntity, double x, double y, double z, float explosionPower, int extraDamage, boolean shouldCauseFire, boolean shouldDamageTerrain, boolean shouldDamageShooter, boolean silkTouch, int fortuneLevel) {
        super(world, explodingEntity, x, y, z, explosionPower, shouldCauseFire, shouldDamageTerrain ? DestructionType.BREAK : DestructionType.NONE);
        this.random = new Random();
        this.world = world;
        this.exploder = explodingEntity;
        this.shootingEntity = shootingEntity;
        this.explosionPower = explosionPower;
        this.extraDamage = extraDamage;
        this.x = x;
        this.y = y;
        this.z = z;
        this.shouldCauseFire = shouldCauseFire;
        this.shouldDamageTerrain = shouldDamageTerrain;
        this.shouldDamageShooter = shouldDamageShooter;
        if (silkTouch) {
            tool.addEnchantment(Enchantments.SILK_TOUCH, 1);
        } else if (fortuneLevel > 0) {
            tool.addEnchantment(Enchantments.FORTUNE, fortuneLevel);
        }
    }

    public void detonate() {
        if (!net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, this)) {
            collectBlocksAndDamageEntities();

            if (world instanceof ServerWorld) {
                affectWorld(false);
                if (!shouldDamageTerrain) {
                    clearAffectedBlocks();
                }

                for (PlayerEntity entityplayer : world.getPlayers()) {
                    if (entityplayer.squaredDistanceTo(x, y, z) < 4096) {
                        ((ServerPlayerEntity) entityplayer).networkHandler.sendPacket(new ExplosionS2CPacket(x, y, z, explosionPower, getAffectedBlocks(), getAffectedPlayers().get(entityplayer)));
                    }
                }
            } else {
                affectWorld(true);
            }
        }
    }

    @Override
    public void collectBlocksAndDamageEntities() {
        calculateAffectedBlockPositions();
        damageEntities();
    }

    protected void calculateAffectedBlockPositions() {
        Set<BlockPos> affectedBlockPositions = Sets.newHashSet();
        for (int x = 0; x < 16; ++x) {
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15) {
                        affectedBlockPositions.addAll(calculateAffectedBlockPositionsInDirection(x, y, z));
                    }
                }
            }
        }
        getAffectedBlocks().addAll(affectedBlockPositions);
    }

    protected Set<BlockPos> calculateAffectedBlockPositionsInDirection(int directionX, int directionY, int directionZ) {
        Set<BlockPos> affectedBlockPositions = new HashSet<>();

        double motionX = (directionX / 15F * 2 - 1);
        double motionY = (directionY / 15F * 2 - 1);
        double motionZ = (directionZ / 15F * 2 - 1);
        double distance = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX = motionX / distance;
        motionY = motionY / distance;
        motionZ = motionZ / distance;
        float remainingExplosionPower = explosionPower * (0.7F + world.random.nextFloat() * 0.6F);
        double posX = x;
        double posY = y;
        double posZ = z;

        while (remainingExplosionPower > 0) {
            BlockPos pos = new BlockPos(posX, posY, posZ);
            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);

            if (!blockState.isAir(world, pos) || !fluidState.isEmpty()) {
                float explosionResistance = Math.max(blockState.getExplosionResistance(world, pos, exploder, this), fluidState.getExplosionResistance(world, pos, exploder, this));
                if (exploder != null) {
                    explosionResistance = exploder.getEffectiveExplosionResistance(this, world, pos, blockState, fluidState, explosionResistance);
                }

                remainingExplosionPower -= (explosionResistance + 0.3F) * 0.3F;
            }

            if (remainingExplosionPower > 0 && (exploder == null || exploder.canExplosionDestroyBlock(this, world, pos, blockState, remainingExplosionPower))) {
                affectedBlockPositions.add(pos);
            }

            posX += motionX * 0.3;
            posY += motionY * 0.3;
            posZ += motionZ * 0.3;
            remainingExplosionPower -= 0.225;
        }

        return affectedBlockPositions;
    }

    protected void damageEntities() {
        float explosionPower = this.explosionPower * 2;

        List<Entity> affectedEntities = world.getEntities(exploder, new Box(MathHelper.floor(x - explosionPower - 1), MathHelper.floor(y - explosionPower - 1), MathHelper.floor(z - explosionPower - 1), MathHelper.floor(x + explosionPower + 1), MathHelper.floor(y + explosionPower + 1), MathHelper.floor(z + explosionPower + 1)));
        net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(world, this, affectedEntities, explosionPower);
        Vec3d vec3d = new Vec3d(x, y, z);

        for (Entity entity : affectedEntities) {
            if (!entity.isImmuneToExplosion()) {
                double relativeDistance = entity.squaredDistanceTo(x, y, z) / explosionPower;

                if (relativeDistance <= 1) {
                    double distanceX = entity.x - x;
                    double distanceY = entity.y + entity.getStandingEyeHeight() - y;
                    double distanceZ = entity.z - z;
                    double distance = MathHelper.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);

                    if (distance != 0) {
                        distanceX = distanceX / distance;
                        distanceY = distanceY / distance;
                        distanceZ = distanceZ / distance;
                        double blockDensity = getExposure(vec3d, entity);
                        double damageMultiplier = (1 - relativeDistance) * blockDensity;
                        damageMultiplier = damageMultiplier * damageMultiplier + damageMultiplier;
                        if (entity == shootingEntity && !shouldDamageShooter) {
                            damageMultiplier /= 32;
                        }
                        entity.damage(DamageSource.explosion(this), (float) ((int) (damageMultiplier / 2 * 7 * explosionPower + 1)) + extraDamage * 1.25F);
                        double knockbackMultiplier = damageMultiplier;

                        if (entity instanceof LivingEntity) {
                            knockbackMultiplier = ProtectionEnchantment.transformExplosionKnockback((LivingEntity) entity, damageMultiplier);
                        }

                        entity.setVelocity(entity.getVelocity().add(new Vec3d(distanceX, distanceY, distanceZ).multiply(knockbackMultiplier)));

                        if (entity instanceof PlayerEntity) {
                            PlayerEntity entityplayer = (PlayerEntity) entity;

                            if (!entityplayer.isSpectator() && (!entityplayer.isCreative() || !entityplayer.abilities.flying)) {
                                getAffectedPlayers().put(entityplayer, new Vec3d(distanceX * damageMultiplier, distanceY * damageMultiplier, distanceZ * damageMultiplier));
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void affectWorld(boolean spawnParticles) {
        this.world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4, (1 + (world.random.nextFloat() - world.random.nextFloat()) * 0.2F) * 0.7F);

        if (spawnParticles) {
            spawnParticles();
        }

        if (shouldDamageTerrain) {

            for (BlockPos pos : getAffectedBlocks()) {
                BlockState blockstate = world.getBlockState(pos);

                if (spawnParticles) {
                    spawnParticles(pos);
                }

                if (!blockstate.isAir(this.world, pos)) {
                    if (this.world instanceof ServerWorld && blockstate.canDropFromExplosion(world, pos, this)) {
                        BlockEntity tileentity = blockstate.hasTileEntity() ? world.getBlockEntity(pos) : null;
                        LootContext.Builder builder = (new LootContext.Builder((ServerWorld) world)).setRandom(world.random).put(LootContextParameters.field_1232, pos).put(LootContextParameters.field_1229, tool).putNullable(LootContextParameters.field_1228, tileentity);

                        Block.dropStacks(blockstate, builder);
                    }

                    blockstate.onBlockExploded(world, pos, this);
                }
            }
        }

        if (this.shouldCauseFire) {
            for (BlockPos pos : getAffectedBlocks()) {
                if (world.getBlockState(pos).isAir(world, pos) && world.getBlockState(pos.down()).isFullOpaque(world, pos.down()) && random.nextInt(3) == 0) {
                    world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }
            }
        }
    }

    protected void spawnParticles(BlockPos pos) {
        double xCoord = pos.getX() + world.random.nextFloat();
        double yCoord = pos.getY() + world.random.nextFloat();
        double zCoord = pos.getZ() + world.random.nextFloat();
        double xSpeed = xCoord - x;
        double ySpeed = yCoord - y;
        double zSpeed = zCoord - z;
        double speed = MathHelper.sqrt(xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed);
        xSpeed = xSpeed / speed;
        ySpeed = ySpeed / speed;
        zSpeed = zSpeed / speed;
        double speedMultiplier = 0.5 / (speed / explosionPower + 0.1);
        speedMultiplier = speedMultiplier * (world.random.nextFloat() * world.random.nextFloat() + 0.3);
        xSpeed = xSpeed * speedMultiplier;
        ySpeed = ySpeed * speedMultiplier;
        zSpeed = zSpeed * speedMultiplier;
        world.addParticle(ParticleTypes.POOF, (xCoord + x) / 2, (yCoord + y) / 2, (zCoord + z) / 2, xSpeed, ySpeed, zSpeed);
        world.addParticle(ParticleTypes.SMOKE, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed);
    }

    protected void spawnParticles() {
        if (explosionPower >= 2 && shouldDamageTerrain) {
            world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0, 0);
        } else {
            world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 1, 0, 0);
        }
    }

    @Override
    public LivingEntity getCausingEntity() {
        return shootingEntity != null ? shootingEntity : super.getCausingEntity();
    }
}
