package jackolauncher.entity;

import jackolauncher.JackOLauncher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JackOProjectileEntity extends Entity implements Projectile {

    private static final TrackedData<Integer> BOUNCES_LEFT = DataTracker.registerData(JackOProjectileEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_FLAMING = DataTracker.registerData(JackOProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_SMOKING = DataTracker.registerData(JackOProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_ENDER_PEARL = DataTracker.registerData(JackOProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_BONE_MEAL = DataTracker.registerData(JackOProjectileEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<CompoundTag> FIREWORKS = DataTracker.registerData(JackOProjectileEntity.class, TrackedDataHandlerRegistry.TAG_COMPOUND);
    private static final TrackedData<ItemStack> POTION_STACK = DataTracker.registerData(JackOProjectileEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Optional<BlockState>> BLOCKSTATE = DataTracker.registerData(JackOProjectileEntity.class, TrackedDataHandlerRegistry.OPTIONAL_BLOCK_STATE);

    protected int ticksInAir;
    protected boolean shouldDamageShooter;
    private int ticksInAirMax;
    protected int randomRotationOffset;
    private UUID shootingEntity;
    private int explosionPower = 2;
    private int extraDamage;
    private boolean shouldDamageTerrain = true;
    private ItemStack arrowStack = ItemStack.EMPTY;
    private boolean hasSilkTouch = false;
    private int fortuneLevel = 0;

    public JackOProjectileEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        randomRotationOffset = random.nextInt(1000);
    }

    public JackOProjectileEntity(World world) {
        this(JackOLauncher.JACK_O_PROJECTILE_ENTITY_TYPE, world);
    }

    public JackOProjectileEntity(World world, double x, double y, double z, CompoundTag ammoProperties) {
        this(world);
        updatePosition(x, y, z);
        readCustomDataFromTag(ammoProperties);
    }

    public JackOProjectileEntity(World world, LivingEntity shootingEntity, CompoundTag ammoProperties, boolean shouldDamageShooter) {
        this(world, shootingEntity.x, shootingEntity.y + shootingEntity.getStandingEyeHeight() - 0.8 / 2, shootingEntity.z, ammoProperties);
        this.shootingEntity = shootingEntity.getUuid();
        this.shouldDamageShooter = shouldDamageShooter;
    }

    public BlockState getBlockState() {
        return dataTracker.get(BLOCKSTATE).orElse(Blocks.JACK_O_LANTERN.getDefaultState());
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(BOUNCES_LEFT, 0);
        dataTracker.startTracking(IS_FLAMING, false);
        dataTracker.startTracking(IS_SMOKING, false);
        dataTracker.startTracking(IS_BONE_MEAL, false);
        dataTracker.startTracking(FIREWORKS, new CompoundTag());
        dataTracker.startTracking(POTION_STACK, ItemStack.EMPTY);
        dataTracker.startTracking(IS_ENDER_PEARL, false);
        dataTracker.startTracking(BLOCKSTATE, Optional.empty());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag compound) {
        ticksInAir = compound.getInt("TicksInAir");
        extraDamage = compound.getByte("ExtraDamage");
        fortuneLevel = compound.getByte("FortuneLevel");
        shouldDamageShooter = compound.getBoolean("ShouldDamageShooter");
        hasSilkTouch = compound.getBoolean("HasSilkTouch");
        shouldDamageTerrain = !compound.contains("ShouldDamageTerrain") || compound.getBoolean("ShouldDamageTerrain");


        if (compound.containsUuid("ShootingEntity")) {
            shootingEntity = compound.getUuid("ShootingEntity");
        }

        dataTracker.set(IS_FLAMING, compound.getBoolean("IsFlaming"));
        dataTracker.set(IS_BONE_MEAL, compound.getBoolean("IsBoneMeal"));
        dataTracker.set(IS_ENDER_PEARL, compound.getBoolean("IsEnderPearl"));
        dataTracker.set(BOUNCES_LEFT, (int) compound.getByte("BouncesAmount"));

        if (compound.contains("ExplosionPower")) {
            explosionPower = compound.getByte("ExplosionPower");
        }
        dataTracker.set(IS_SMOKING, explosionPower > 0);

        CompoundTag arrowNBT = compound.getCompound("Arrows");
        if (!arrowNBT.isEmpty()) {
            arrowStack = ItemStack.fromTag(arrowNBT);
        }
        CompoundTag potionNBT = compound.getCompound("Potion");
        if (!potionNBT.isEmpty()) {
            dataTracker.set(POTION_STACK, ItemStack.fromTag(potionNBT));
        }
        BlockState blockState = NbtHelper.toBlockState(compound.getCompound("BlockState"));
        if (!(blockState == Blocks.AIR.getDefaultState())) {
            dataTracker.set(BLOCKSTATE, Optional.of(blockState));
        }
        CompoundTag fireworksNBT = compound.getCompound("Fireworks");
        if (!fireworksNBT.isEmpty()) {
            ticksInAirMax = 6 * (fireworksNBT.getByte("Flight") + 1) + random.nextInt(5);
            dataTracker.set(FIREWORKS, fireworksNBT);
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag compound) {
        compound.putInt("TicksInAir", ticksInAir);
        compound.putByte("FortuneLevel", (byte) fortuneLevel);
        compound.putByte("ExtraDamage", (byte) extraDamage);
        compound.putByte("ExplosionPower", (byte) explosionPower);
        compound.putByte("BouncesLeft", dataTracker.get(BOUNCES_LEFT).byteValue());
        compound.putBoolean("HasSilkTouch", hasSilkTouch);
        compound.putBoolean("ShouldDamageTerrain", shouldDamageTerrain);
        compound.putBoolean("IsFiery", dataTracker.get(IS_FLAMING));
        compound.putBoolean("IsEnderPearl", dataTracker.get(IS_ENDER_PEARL));
        compound.putBoolean("IsBoneMeal", dataTracker.get(IS_BONE_MEAL));
        compound.putBoolean("ShouldDamageShooter", shouldDamageShooter);
        compound.put("Fireworks", dataTracker.get(FIREWORKS));
        compound.put("Arrows", arrowStack.toTag(new CompoundTag()));
        compound.put("Potion", dataTracker.get(POTION_STACK).toTag(new CompoundTag()));
        compound.put("BlockState", NbtHelper.fromBlockState(dataTracker.get(BLOCKSTATE).orElse(Blocks.AIR.getDefaultState())));
        if (shootingEntity != null) {
            compound.putUuid("ShootingEntity", shootingEntity);
        }
    }

    public LivingEntity getShootingEntity() {
        if (shootingEntity == null || !(world instanceof ServerWorld)) {
            return null;
        }
        Entity shootingEntity = ((ServerWorld) world).getEntity(this.shootingEntity);
        return shootingEntity instanceof LivingEntity ? (LivingEntity) shootingEntity : null;
    }

    @Override
    public void setVelocity(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        double motionX = x * velocity / f + random.nextGaussian() * 0.0075 * inaccuracy;
        double motionY = y * velocity / f + random.nextGaussian() * 0.0075 * inaccuracy;
        double motionZ = z * velocity / f + random.nextGaussian() * 0.0075 * inaccuracy;
        setVelocity(motionX, motionY, motionZ);
    }

    public void setVelocity(Entity shooter, float pitch, float yaw, float velocity, float inaccuracy) {
        float x = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float y = -MathHelper.sin(pitch * 0.017453292F);
        float z = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        setVelocity(x, y, z, velocity, inaccuracy);
        setVelocity(getVelocity().add(shooter.getVelocity()));
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity shootingEntity = getShootingEntity();

        if (shootingEntity != null && !shootingEntity.isAlive() && dataTracker.get(IS_ENDER_PEARL)) {
            dataTracker.set(IS_ENDER_PEARL, false);
        }

        if (!world.isClient && !dataTracker.get(FIREWORKS).isEmpty()) {
            if (ticksInAir == 0) {
                world.playSound(null, x, y, z, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.NEUTRAL, 2, 1);
            }
            if (ticksInAir > ticksInAirMax) {
                detonate(null);
            }
        }

        ++ticksInAir;
        spawnParticles();

        HitResult rayTraceResult = ProjectileUtil.getCollision(this, true, ticksInAir >= 5, shootingEntity, RayTraceContext.ShapeType.COLLIDER);
        //noinspection ConstantConditions
        if (rayTraceResult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
            onImpact(rayTraceResult);
        }

        Vec3d motion = getVelocity();
        if (dataTracker.get(FIREWORKS).isEmpty()) {
            motion = motion.subtract(0, 0.08, 0);
            if (isTouchingWater()) {
                motion = motion.multiply(0.9);
            }
        }
        setVelocity(motion);

        x += motion.x;
        y += motion.y;
        z += motion.z;

        updatePosition(x, y, z);

        checkBlockCollision();
    }

    private void onImpact(HitResult rayTraceResult) {
        if (!world.isClient) {
            LivingEntity shootingEntity = getShootingEntity();

            if (rayTraceResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) rayTraceResult).getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) ((EntityHitResult) rayTraceResult).getEntity();
                if (entity == shootingEntity && ticksInAir < 5) {
                    return;
                }
                entity.damage(new ProjectileDamageSource(JackOLauncher.MODID + ".jack_o_projectile_impact", this, shootingEntity), 1 + 2 * extraDamage);

                if (dataTracker.get(IS_FLAMING)) {
                    entity.setOnFireFor(4);
                }
            }
            if (rayTraceResult instanceof BlockHitResult) {
                BlockHitResult blockRayTrace = (BlockHitResult) rayTraceResult;
                if (dataTracker.get(IS_FLAMING) && world.isAir(blockRayTrace.getBlockPos().offset(blockRayTrace.getSide())) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity)) {
                    world.setBlockState(blockRayTrace.getBlockPos().offset(blockRayTrace.getSide()), Blocks.FIRE.getDefaultState(), 11);
                }
            }
            if (dataTracker.get(BOUNCES_LEFT) <= 0 || isTouchingWater()) {
                detonate(rayTraceResult);
                return;
            }
        }
        if (dataTracker.get(BOUNCES_LEFT) > 0 && !isTouchingWater()) {
            bounce(rayTraceResult);
        }
    }

    private void bounce(HitResult rayTraceResult) {
        dataTracker.set(BOUNCES_LEFT, dataTracker.get(BOUNCES_LEFT) - 1);
        world.playSound(null, x, y, z, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 1, 1);
        if (rayTraceResult instanceof BlockHitResult) {
            Direction.Axis axis = ((BlockHitResult) rayTraceResult).getSide().getAxis();
            Vec3d motion = getVelocity();
            if (axis == Direction.Axis.X) {
                setVelocity(-motion.x * 0.75, motion.y, motion.z);
            } else if (axis == Direction.Axis.Y) {
                setVelocity(motion.x, -motion.y * 0.75, motion.z);
            } else if (axis == Direction.Axis.Z) {
                setVelocity(motion.x, motion.y, -motion.z * 0.75);
            }
            world.sendEntityStatus(this, (byte) 100);
            if (!world.isClient && dataTracker.get(IS_BONE_MEAL)) {
                // noinspection deprecation
                if (BoneMealItem.useOnFertilizable(new ItemStack(Items.BONE_MEAL), world, ((BlockHitResult) rayTraceResult).getBlockPos())) {
                    world.playLevelEvent(2005, ((BlockHitResult) rayTraceResult).getBlockPos(), 0);
                }
            }
        } else if (rayTraceResult.getType() == HitResult.Type.ENTITY) {
            detonate(rayTraceResult);
        }
    }

    private void detonate(HitResult rayTraceResult) {
        if (!world.isClient) {
            LivingEntity shootingEntity = getShootingEntity();

            if (dataTracker.get(IS_ENDER_PEARL)) {
                doEnderPearlThings(rayTraceResult);
            }
            if (arrowStack != null && !arrowStack.isEmpty()) {
                spawnArrows(rayTraceResult);
            }

            boolean canMobGrief = shootingEntity == null || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity);
            if (explosionPower > 0) {
                new CustomExplosion(world, this, shootingEntity, x, y, z, (explosionPower + 2) / 2.25F, extraDamage, canMobGrief && dataTracker.get(IS_FLAMING), canMobGrief && shouldDamageTerrain, !shouldDamageShooter, hasSilkTouch, fortuneLevel).detonate();
            } else {
                world.sendEntityStatus(this, (byte) 101);
                world.playSound(null, x, y, z, getBlockState().getSoundType(world, new BlockPos(x, y, z), null).getBreakSound(), SoundCategory.NEUTRAL, 1, 1);
            }

            if (dataTracker.get(IS_BONE_MEAL)) {
                doBoneMealThings();
            }

            if (!dataTracker.get(POTION_STACK).isEmpty() && (dataTracker.get(POTION_STACK).getItem() == Items.SPLASH_POTION || dataTracker.get(POTION_STACK).getItem() == Items.LINGERING_POTION)) {
                doPotionThings(rayTraceResult);
            }
            if (!dataTracker.get(FIREWORKS).isEmpty()) {
                dealFireworkExplosionDamage();
                world.sendEntityStatus(this, (byte) 17);
            }
            remove();
        }
    }

    private void doEnderPearlThings(HitResult rayTraceResult) {
        LivingEntity shootingEntity = getShootingEntity();
        if (shootingEntity == null || !shootingEntity.isAlive() || shootingEntity.dimension != dimension) {
            return;
        }

        if (rayTraceResult instanceof EntityHitResult) {
            if (((EntityHitResult) rayTraceResult).getEntity() == shootingEntity) {
                return;
            }
            ((EntityHitResult) rayTraceResult).getEntity().damage(DamageSource.thrownProjectile(this, shootingEntity), 0.0F);
        }

        for (int i = 0; i < 32; ++i) {
            world.addParticle(ParticleTypes.PORTAL, x, y + random.nextDouble() * 2.0D, z, random.nextGaussian(), 0.0D, random.nextGaussian());
        }

        if (!world.isClient) {
            teleportEntity(shootingEntity, x, y, z);
        }
    }

    private void teleportEntity(LivingEntity entity, double posX, double posY, double posZ) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity entityplayermp = (ServerPlayerEntity) entity;

            if (entityplayermp.networkHandler.getConnection().isOpen() && entityplayermp.world == world && !entityplayermp.isSleeping()) {
                entity.stopRiding();
                entity.requestTeleport(posX, posY, posZ);
                entity.fallDistance = 0;
                entity.damage(DamageSource.FALL, 3);
            }
        } else {
            entity.requestTeleport(posX, posY, posZ);
            entity.fallDistance = 0;
        }
    }

    private void spawnArrows(HitResult rayTraceResult) {
        LivingEntity shootingEntity = getShootingEntity();

        for (int i = 0; i < arrowStack.getCount(); i++) {
            ProjectileEntity arrow;
            if (shootingEntity != null) {
                arrow = ((ArrowItem) arrowStack.getItem()).createArrow(world, arrowStack, shootingEntity);
            } else {
                if (!(world instanceof ServerWorld)) {
                    return;
                }
                arrow = ((ArrowItem) arrowStack.getItem()).createArrow(world, arrowStack, FakePlayerFactory.getMinecraft((ServerWorld) world));
            }
            arrow.x = x;
            arrow.y = y;
            arrow.z = z;
            arrow.pickupType = ArrowEntity.PickupPermission.CREATIVE_ONLY;
            arrow.setDamage(arrow.getDamage() * 2.5);
            if (shootingEntity != null) {
                arrow.ownerUuid = this.shootingEntity;
            }
            Vec3d motion = getVelocity();
            double x = motion.x;
            double y = motion.y;
            double z = motion.z;
            if (rayTraceResult instanceof BlockHitResult) {
                Direction.Axis axis = ((BlockHitResult) rayTraceResult).getSide().getAxis();
                if (axis == Direction.Axis.X) {
                    x = -motion.x;
                } else if (axis == Direction.Axis.Y) {
                    y = -motion.y;
                } else if (axis == Direction.Axis.Z) {
                    z = -motion.z;
                }
            } else if (rayTraceResult instanceof EntityHitResult) {
                x = random.nextDouble() * 2 - 1;
                y = random.nextDouble();
                z = random.nextDouble() * 2 - 1;
            }
            arrow.setVelocity(x, y, z, (float) motion.length(), 10);
            world.spawnEntity(arrow);
        }
    }

    private void doBoneMealThings() {
        BlockPos.stream((int) (x + 0.5) - 5, (int) (y + 0.5) - 5, (int) (z + 0.5) - 5, (int) (x + 0.5) + 5, (int) (y + 0.5) + 5, (int) (z + 0.5) + 5).forEach(pos -> {
            if (random.nextInt(8) == 0 && BoneMealItem.useOnFertilizable(new ItemStack(Items.BONE_MEAL), world, pos)) {
                world.playLevelEvent(2005, pos, 0);
            }
        });
    }

    private void doPotionThings(HitResult rayTraceResult) {
        if (!world.isClient) {
            ItemStack stack = dataTracker.get(POTION_STACK);
            Potion potion = PotionUtil.getPotion(stack);
            List<StatusEffectInstance> list = PotionUtil.getPotionEffects(stack);
            boolean isWater = potion == Potions.WATER && list.isEmpty();

            if (rayTraceResult instanceof BlockHitResult && isWater) {
                BlockPos pos = ((BlockHitResult) rayTraceResult).getBlockPos().offset(((BlockHitResult) rayTraceResult).getSide());
                extinguishFires(pos, ((BlockHitResult) rayTraceResult).getSide());

                for (Direction face : Direction.Type.HORIZONTAL) {
                    extinguishFires(pos.offset(face), face);
                }
            }

            if (isWater) {
                applyWater();
            } else if (!list.isEmpty()) {
                if (stack.getItem() == Items.LINGERING_POTION) {
                    makeAreaOfEffectCloud(stack, potion);
                } else {
                    Entity hitEntity = null;
                    if (rayTraceResult instanceof EntityHitResult) {
                        hitEntity = ((EntityHitResult) rayTraceResult).getEntity();
                    }
                    applySplash(list, hitEntity);
                }
            }

            // spawn particles
            int eventType = potion.hasInstantEffect() ? 2007 : 2002;
            world.playLevelEvent(eventType, new BlockPos(this), PotionUtil.getColor(stack));
        }
    }

    private void extinguishFires(BlockPos pos, Direction face) {
        if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
            this.world.method_8506(null, pos.offset(face), face.getOpposite());
        }
    }

    private void applyWater() {
        Box axisalignedbb = getBoundingBox().expand(5, 3, 5);
        List<LivingEntity> list = world.getEntities(LivingEntity.class, axisalignedbb, ThrownPotionEntity.WATER_HURTS);
        if (!list.isEmpty()) {
            for (LivingEntity entitylivingbase : list) {
                double distance = squaredDistanceTo(entitylivingbase);

                if (distance < 16.0D && (entitylivingbase instanceof EndermanEntity || entitylivingbase instanceof BlazeEntity)) {
                    entitylivingbase.damage(DamageSource.DROWN, 1);
                }
            }
        }
    }

    private void applySplash(List<StatusEffectInstance> effectInstances, Entity hitEntity) {
        Box boundingBox = getBoundingBox().expand(4, 2, 4);
        List<LivingEntity> entities = world.getNonSpectatingEntities(LivingEntity.class, boundingBox);

        for (LivingEntity entity : entities) {
            double distance = squaredDistanceTo(entity);
            if (!entity.isAffectedBySplashPotions() || distance >= 16) {
                break;
            }

            double effectMultiplier = 1 - Math.sqrt(distance) / 4;
            if (entity == hitEntity) {
                effectMultiplier = 1;
            }

            for (StatusEffectInstance effectInstance : effectInstances) {
                StatusEffect effect = effectInstance.getEffectType();
                if (effect.isInstant()) {
                    effect.applyInstantEffect(this, getShootingEntity(), entity, effectInstance.getAmplifier(), effectMultiplier);
                } else {
                    int duration = (int) (effectMultiplier * (double) effectInstance.getDuration() + 0.5);
                    if (duration > 20) {
                        entity.addStatusEffect(new StatusEffectInstance(effect, duration, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.shouldShowParticles()));
                    }
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(ItemStack stack, Potion potion) {
        AreaEffectCloudEntity effectCloud = new AreaEffectCloudEntity(world, x, y, z);
        effectCloud.setOwner(getShootingEntity());
        effectCloud.setRadius(3.2F);
        effectCloud.setRadiusOnUse(-0.4F);
        effectCloud.setWaitTime(10);
        effectCloud.setRadiusGrowth(-effectCloud.getRadius() / effectCloud.getDuration());
        effectCloud.setPotion(potion);

        for (StatusEffectInstance effectInstance : PotionUtil.getCustomPotionEffects(stack)) {
            effectCloud.addEffect(new StatusEffectInstance(effectInstance));
        }

        CompoundTag compoundNBT = stack.getTag();
        if (compoundNBT != null && compoundNBT.contains("CustomPotionColor", 99)) {
            effectCloud.setColor(compoundNBT.getInt("CustomPotionColor"));
        }

        world.spawnEntity(effectCloud);
    }

    private void dealFireworkExplosionDamage() {
        int damageMultiplier = 0;
        ListTag explosions = dataTracker.get(FIREWORKS).getList("Explosions", 10);

        if (!explosions.isEmpty()) {
            damageMultiplier = 5 + explosions.size() * 2;
        }

        if (damageMultiplier > 0) {
            Vec3d posVec = new Vec3d(x, y, z);

            for (LivingEntity entity : world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(5))) {
                if (squaredDistanceTo(entity) <= 25) {
                    boolean flag = false;
                    for (int i = 0; i < 2; ++i) {
                        HitResult raytraceresult = world.rayTrace(new RayTraceContext(posVec, new Vec3d(entity.x, entity.y + entity.getHeight() * 0.5 * i, entity.z), RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this));
                        if (raytraceresult.getType() == HitResult.Type.MISS) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        entity.damage(DamageSource.FIREWORKS, damageMultiplier * (float) Math.sqrt((5 - distanceTo(entity)) / 5));
                    }
                }
            }
        }
    }

    private void spawnParticles() {
        if (world.isClient) {
            if (isTouchingWater()) {
                for (int i = 0; i < 4; ++i) {
                    world.addParticle(ParticleTypes.BUBBLE, x - getVelocity().getX() * 0.25, y - getVelocity().getY() * 0.25, z - getVelocity().getZ() * 0.25, getVelocity().getX(), getVelocity().getY(), getVelocity().getZ());
                }
            } else {
                if (dataTracker.get(IS_FLAMING)) {
                    spawnParticle(ParticleTypes.FLAME, 0.25, 0.6, 0);
                }
                if (dataTracker.get(IS_SMOKING)) {
                    for (int i = 0; i < 3; i++) {
                        spawnParticle(ParticleTypes.SMOKE, 0.25, 0.3, 0);
                    }
                    if (age % 2 == 0) {
                        spawnParticle(ParticleTypes.LARGE_SMOKE, 0.4, 0.3, 0);
                    }
                }
                if (!dataTracker.get(FIREWORKS).isEmpty()) {
                    world.addParticle(ParticleTypes.FIREWORK, x, y, z, random.nextGaussian() * 0.05, -getVelocity().getY() * 0.5, random.nextGaussian() * 0.05);
                }
                if (dataTracker.get(IS_BONE_MEAL) && age % 3 == 0) {
                    spawnParticle(ParticleTypes.HAPPY_VILLAGER, 0.1, 0, 0.02);
                }
                if (dataTracker.get(IS_ENDER_PEARL)) {
                    spawnParticle(ParticleTypes.PORTAL, 0.3, 0, 0.08);
                }
                if (!dataTracker.get(POTION_STACK).isEmpty()) {
                    int color = PotionUtil.getColor(dataTracker.get(POTION_STACK));
                    if (color > 0) {
                        world.addImportantParticle(ParticleTypes.ENTITY_EFFECT, x + (random.nextDouble() - 0.5) * getWidth(), y + random.nextDouble() * getHeight(), z + (random.nextDouble() - 0.5) * getWidth(), (color >> 16 & 255) / 255D, (color >> 8 & 255) / 255D, (color & 255) / 255D);
                    }
                }
            }
        }
    }

    private void spawnParticle(ParticleEffect particle, double spreadMultiplier, double motionMultiplier, double motionSpreadMultiplier) {
        world.addParticle(particle, x + random.nextGaussian() * spreadMultiplier, y + random.nextGaussian() * spreadMultiplier + 0.5, z + random.nextGaussian() * spreadMultiplier, getVelocity().getX() * motionMultiplier + random.nextGaussian() * motionSpreadMultiplier, getVelocity().getY() * motionMultiplier + random.nextGaussian() * motionSpreadMultiplier, getVelocity().getZ() * motionMultiplier + random.nextGaussian() * motionSpreadMultiplier);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void handleStatus(byte id) {
        switch (id) {
            case (17):
                world.addFireworkParticle(x, y, z, getVelocity().getX(), getVelocity().getY(), getVelocity().getZ(), dataTracker.get(FIREWORKS));
                break;
            case (100):
                for (int j = 0; j < 16; ++j) {
                    float rotationXZ = (float) (random.nextFloat() * Math.PI * 2);
                    float rotationY = (float) (random.nextFloat() * Math.PI);
                    float distance = random.nextFloat() * 0.4F + 0.3F;
                    float x = MathHelper.sin(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float y = MathHelper.cos(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float z = MathHelper.cos(rotationY) * distance;
                    world.addParticle(ParticleTypes.ITEM_SLIME, x + x, y + y, z + z, 0, 0, 0);
                }
                break;
            case (101):
                for (int j = 0; j < 48; ++j) {
                    float rotationXZ = (float) (random.nextFloat() * Math.PI * 2);
                    float rotationY = (float) (random.nextFloat() * Math.PI);
                    float distance = random.nextFloat() * 0.4F + 0.3F;
                    float x = MathHelper.sin(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float y = MathHelper.cos(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float z = MathHelper.cos(rotationY) * distance;
                    world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, getBlockState()), x + x, y + y, z + z, 0, 0, 0);
                }
                break;
            default:
                super.handleStatus(id);
                break;
        }
    }
}
