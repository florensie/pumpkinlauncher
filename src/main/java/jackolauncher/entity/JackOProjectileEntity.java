package jackolauncher.entity;

import jackolauncher.JackOLauncher;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/*
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JackOProjectileEntity extends Entity implements IProjectile {

    private static final DataParameter<Integer> BOUNCES_LEFT = EntityDataManager.createKey(JackOProjectileEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> IS_FLAMING = EntityDataManager.createKey(JackOProjectileEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SMOKING = EntityDataManager.createKey(JackOProjectileEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_ENDER_PEARL = EntityDataManager.createKey(JackOProjectileEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_BONE_MEAL = EntityDataManager.createKey(JackOProjectileEntity.class, DataSerializers.BOOLEAN);
    private static final DataParameter<CompoundNBT> FIREWORKS = EntityDataManager.createKey(JackOProjectileEntity.class, DataSerializers.COMPOUND_NBT);
    private static final DataParameter<ItemStack> POTION_STACK = EntityDataManager.createKey(JackOProjectileEntity.class, DataSerializers.ITEMSTACK);
    private static final DataParameter<Optional<BlockState>> BLOCKSTATE = EntityDataManager.createKey(JackOProjectileEntity.class, DataSerializers.OPTIONAL_BLOCK_STATE);

    protected int ticksInAir;
    protected boolean shouldDamageShooter;
    private int ticksInAirMax;
    protected int randomRotationOffset;
    @Nullable
    private UUID shootingEntity;
    private int explosionPower = 2;
    private int extraDamage;
    private boolean shouldDamageTerrain = true;
    private ItemStack arrowStack = ItemStack.EMPTY;
    private boolean hasSilkTouch = false;
    private int fortuneLevel = 0;

    public JackOProjectileEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        randomRotationOffset = rand.nextInt(1000);
    }

    public JackOProjectileEntity(World world) {
        this(JackOLauncher.JACK_O_PROJECTILE_ENTITY_TYPE, world);
    }

    public JackOProjectileEntity(World world, double x, double y, double z, CompoundNBT ammoProperties) {
        this(world);
        setPosition(x, y, z);
        readAdditional(ammoProperties);
    }

    public JackOProjectileEntity(World world, LivingEntity shootingEntity, CompoundNBT ammoProperties, boolean shouldDamageShooter) {
        this(world, shootingEntity.posX, shootingEntity.posY + shootingEntity.getEyeHeight() - 0.8 / 2, shootingEntity.posZ, ammoProperties);
        this.shootingEntity = shootingEntity.getUniqueID();
        this.shouldDamageShooter = shouldDamageShooter;
    }

    public BlockState getBlockState() {
        return dataManager.get(BLOCKSTATE).orElse(Blocks.JACK_O_LANTERN.getDefaultState());
    }

    @Override
    protected void registerData() {
        dataManager.register(BOUNCES_LEFT, 0);
        dataManager.register(IS_FLAMING, false);
        dataManager.register(IS_SMOKING, false);
        dataManager.register(IS_BONE_MEAL, false);
        dataManager.register(FIREWORKS, new CompoundNBT());
        dataManager.register(POTION_STACK, ItemStack.EMPTY);
        dataManager.register(IS_ENDER_PEARL, false);
        dataManager.register(BLOCKSTATE, Optional.empty());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        ticksInAir = compound.getInt("TicksInAir");
        extraDamage = compound.getByte("ExtraDamage");
        fortuneLevel = compound.getByte("FortuneLevel");
        shouldDamageShooter = compound.getBoolean("ShouldDamageShooter");
        hasSilkTouch = compound.getBoolean("HasSilkTouch");
        shouldDamageTerrain = !compound.contains("ShouldDamageTerrain") || compound.getBoolean("ShouldDamageTerrain");


        if (compound.hasUniqueId("ShootingEntity")) {
            shootingEntity = compound.getUniqueId("ShootingEntity");
        }

        dataManager.set(IS_FLAMING, compound.getBoolean("IsFlaming"));
        dataManager.set(IS_BONE_MEAL, compound.getBoolean("IsBoneMeal"));
        dataManager.set(IS_ENDER_PEARL, compound.getBoolean("IsEnderPearl"));
        dataManager.set(BOUNCES_LEFT, (int) compound.getByte("BouncesAmount"));

        if (compound.contains("ExplosionPower")) {
            explosionPower = compound.getByte("ExplosionPower");
        }
        dataManager.set(IS_SMOKING, explosionPower > 0);

        CompoundNBT arrowNBT = compound.getCompound("Arrows");
        if (!arrowNBT.isEmpty()) {
            arrowStack = ItemStack.read(arrowNBT);
        }
        CompoundNBT potionNBT = compound.getCompound("Potion");
        if (!potionNBT.isEmpty()) {
            dataManager.set(POTION_STACK, ItemStack.read(potionNBT));
        }
        BlockState blockState = NBTUtil.readBlockState(compound.getCompound("BlockState"));
        if (!(blockState == Blocks.AIR.getDefaultState())) {
            dataManager.set(BLOCKSTATE, Optional.of(blockState));
        }
        CompoundNBT fireworksNBT = compound.getCompound("Fireworks");
        if (!fireworksNBT.isEmpty()) {
            ticksInAirMax = 6 * (fireworksNBT.getByte("Flight") + 1) + rand.nextInt(5);
            dataManager.set(FIREWORKS, fireworksNBT);
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        compound.putInt("TicksInAir", ticksInAir);
        compound.putByte("FortuneLevel", (byte) fortuneLevel);
        compound.putByte("ExtraDamage", (byte) extraDamage);
        compound.putByte("ExplosionPower", (byte) explosionPower);
        compound.putByte("BouncesLeft", dataManager.get(BOUNCES_LEFT).byteValue());
        compound.putBoolean("HasSilkTouch", hasSilkTouch);
        compound.putBoolean("ShouldDamageTerrain", shouldDamageTerrain);
        compound.putBoolean("IsFiery", dataManager.get(IS_FLAMING));
        compound.putBoolean("IsEnderPearl", dataManager.get(IS_ENDER_PEARL));
        compound.putBoolean("IsBoneMeal", dataManager.get(IS_BONE_MEAL));
        compound.putBoolean("ShouldDamageShooter", shouldDamageShooter);
        compound.put("Fireworks", dataManager.get(FIREWORKS));
        compound.put("Arrows", arrowStack.write(new CompoundNBT()));
        compound.put("Potion", dataManager.get(POTION_STACK).write(new CompoundNBT()));
        compound.put("BlockState", NBTUtil.writeBlockState(dataManager.get(BLOCKSTATE).orElse(Blocks.AIR.getDefaultState())));
        if (shootingEntity != null) {
            compound.putUniqueId("ShootingEntity", shootingEntity);
        }
    }

    @Nullable
    public LivingEntity getShootingEntity() {
        if (shootingEntity == null || !(world instanceof ServerWorld)) {
            return null;
        }
        Entity shootingEntity = ((ServerWorld) world).getEntityByUuid(this.shootingEntity);
        return shootingEntity instanceof LivingEntity ? (LivingEntity) shootingEntity : null;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        double motionX = x * velocity / f + rand.nextGaussian() * 0.0075 * inaccuracy;
        double motionY = y * velocity / f + rand.nextGaussian() * 0.0075 * inaccuracy;
        double motionZ = z * velocity / f + rand.nextGaussian() * 0.0075 * inaccuracy;
        setMotion(motionX, motionY, motionZ);
    }

    public void shoot(Entity shooter, float pitch, float yaw, float velocity, float inaccuracy) {
        float x = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float y = -MathHelper.sin(pitch * 0.017453292F);
        float z = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        shoot(x, y, z, velocity, inaccuracy);
        setMotion(getMotion().add(shooter.getMotion()));
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity shootingEntity = getShootingEntity();

        if (shootingEntity != null && !shootingEntity.isAlive() && dataManager.get(IS_ENDER_PEARL)) {
            dataManager.set(IS_ENDER_PEARL, false);
        }

        if (!world.isRemote && !dataManager.get(FIREWORKS).isEmpty()) {
            if (ticksInAir == 0) {
                world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.NEUTRAL, 2, 1);
            }
            if (ticksInAir > ticksInAirMax) {
                detonate(null);
            }
        }

        ++ticksInAir;
        spawnParticles();

        RayTraceResult rayTraceResult = ProjectileHelper.func_221266_a(this, true, ticksInAir >= 5, shootingEntity, RayTraceContext.BlockMode.COLLIDER);
        //noinspection ConstantConditions
        if (rayTraceResult.getType() != RayTraceResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
            onImpact(rayTraceResult);
        }

        Vec3d motion = getMotion();
        if (dataManager.get(FIREWORKS).isEmpty()) {
            motion = motion.subtract(0, 0.08, 0);
            if (isInWater()) {
                motion = motion.scale(0.9);
            }
        }
        setMotion(motion);

        posX += motion.x;
        posY += motion.y;
        posZ += motion.z;

        setPosition(posX, posY, posZ);

        doBlockCollisions();
    }

    private void onImpact(RayTraceResult rayTraceResult) {
        if (!world.isRemote) {
            LivingEntity shootingEntity = getShootingEntity();

            if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity();
                if (entity == shootingEntity && ticksInAir < 5) {
                    return;
                }
                entity.attackEntityFrom(new IndirectEntityDamageSource(JackOLauncher.MODID + ".jack_o_projectile_impact", this, shootingEntity), 1 + 2 * extraDamage);

                if (dataManager.get(IS_FLAMING)) {
                    entity.setFire(4);
                }
            }
            if (rayTraceResult instanceof BlockRayTraceResult) {
                BlockRayTraceResult blockRayTrace = (BlockRayTraceResult) rayTraceResult;
                if (dataManager.get(IS_FLAMING) && world.isAirBlock(blockRayTrace.getPos().offset(blockRayTrace.getFace())) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity)) {
                    world.setBlockState(blockRayTrace.getPos().offset(blockRayTrace.getFace()), Blocks.FIRE.getDefaultState(), 11);
                }
            }
            if (dataManager.get(BOUNCES_LEFT) <= 0 || isInWater()) {
                detonate(rayTraceResult);
                return;
            }
        }
        if (dataManager.get(BOUNCES_LEFT) > 0 && !isInWater()) {
            bounce(rayTraceResult);
        }
    }

    private void bounce(RayTraceResult rayTraceResult) {
        dataManager.set(BOUNCES_LEFT, dataManager.get(BOUNCES_LEFT) - 1);
        world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_SLIME_JUMP, SoundCategory.NEUTRAL, 1, 1);
        if (rayTraceResult instanceof BlockRayTraceResult) {
            Direction.Axis axis = ((BlockRayTraceResult) rayTraceResult).getFace().getAxis();
            Vec3d motion = getMotion();
            if (axis == Direction.Axis.X) {
                setMotion(-motion.x * 0.75, motion.y, motion.z);
            } else if (axis == Direction.Axis.Y) {
                setMotion(motion.x, -motion.y * 0.75, motion.z);
            } else if (axis == Direction.Axis.Z) {
                setMotion(motion.x, motion.y, -motion.z * 0.75);
            }
            world.setEntityState(this, (byte) 100);
            if (!world.isRemote && dataManager.get(IS_BONE_MEAL)) {
                // noinspection deprecation
                if (BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, ((BlockRayTraceResult) rayTraceResult).getPos())) {
                    world.playEvent(2005, ((BlockRayTraceResult) rayTraceResult).getPos(), 0);
                }
            }
        } else if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY) {
            detonate(rayTraceResult);
        }
    }

    private void detonate(@Nullable RayTraceResult rayTraceResult) {
        if (!world.isRemote) {
            LivingEntity shootingEntity = getShootingEntity();

            if (dataManager.get(IS_ENDER_PEARL)) {
                doEnderPearlThings(rayTraceResult);
            }
            if (arrowStack != null && !arrowStack.isEmpty()) {
                spawnArrows(rayTraceResult);
            }

            boolean canMobGrief = shootingEntity == null || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(world, shootingEntity);
            if (explosionPower > 0) {
                new CustomExplosion(world, this, shootingEntity, posX, posY, posZ, (explosionPower + 2) / 2.25F, extraDamage, canMobGrief && dataManager.get(IS_FLAMING), canMobGrief && shouldDamageTerrain, !shouldDamageShooter, hasSilkTouch, fortuneLevel).detonate();
            } else {
                world.setEntityState(this, (byte) 101);
                world.playSound(null, posX, posY, posZ, getBlockState().getSoundType(world, new BlockPos(posX, posY, posZ), null).getBreakSound(), SoundCategory.NEUTRAL, 1, 1);
            }

            if (dataManager.get(IS_BONE_MEAL)) {
                doBoneMealThings();
            }

            if (!dataManager.get(POTION_STACK).isEmpty() && (dataManager.get(POTION_STACK).getItem() == Items.SPLASH_POTION || dataManager.get(POTION_STACK).getItem() == Items.LINGERING_POTION)) {
                doPotionThings(rayTraceResult);
            }
            if (!dataManager.get(FIREWORKS).isEmpty()) {
                dealFireworkExplosionDamage();
                world.setEntityState(this, (byte) 17);
            }
            remove();
        }
    }

    private void doEnderPearlThings(@Nullable RayTraceResult rayTraceResult) {
        LivingEntity shootingEntity = getShootingEntity();
        if (shootingEntity == null || !shootingEntity.isAlive() || shootingEntity.dimension != dimension) {
            return;
        }

        if (rayTraceResult instanceof EntityRayTraceResult) {
            if (((EntityRayTraceResult) rayTraceResult).getEntity() == shootingEntity) {
                return;
            }
            ((EntityRayTraceResult) rayTraceResult).getEntity().attackEntityFrom(DamageSource.causeThrownDamage(this, shootingEntity), 0.0F);
        }

        for (int i = 0; i < 32; ++i) {
            world.addParticle(ParticleTypes.PORTAL, posX, posY + rand.nextDouble() * 2.0D, posZ, rand.nextGaussian(), 0.0D, rand.nextGaussian());
        }

        if (!world.isRemote) {
            teleportEntity(shootingEntity, posX, posY, posZ);
        }
    }

    private void teleportEntity(LivingEntity entity, double posX, double posY, double posZ) {
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity entityplayermp = (ServerPlayerEntity) entity;

            if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == world && !entityplayermp.isSleeping()) {
                entity.stopRiding();
                entity.setPositionAndUpdate(posX, posY, posZ);
                entity.fallDistance = 0;
                entity.attackEntityFrom(DamageSource.FALL, 3);
            }
        } else {
            entity.setPositionAndUpdate(posX, posY, posZ);
            entity.fallDistance = 0;
        }
    }

    private void spawnArrows(@Nullable RayTraceResult rayTraceResult) {
        LivingEntity shootingEntity = getShootingEntity();

        for (int i = 0; i < arrowStack.getCount(); i++) {
            AbstractArrowEntity arrow;
            if (shootingEntity != null) {
                arrow = ((ArrowItem) arrowStack.getItem()).createArrow(world, arrowStack, shootingEntity);
            } else {
                if (!(world instanceof ServerWorld)) {
                    return;
                }
                arrow = ((ArrowItem) arrowStack.getItem()).createArrow(world, arrowStack, FakePlayerFactory.getMinecraft((ServerWorld) world));
            }
            arrow.posX = posX;
            arrow.posY = posY;
            arrow.posZ = posZ;
            arrow.pickupStatus = ArrowEntity.PickupStatus.CREATIVE_ONLY;
            arrow.setDamage(arrow.getDamage() * 2.5);
            if (shootingEntity != null) {
                arrow.shootingEntity = this.shootingEntity;
            }
            Vec3d motion = getMotion();
            double x = motion.x;
            double y = motion.y;
            double z = motion.z;
            if (rayTraceResult instanceof BlockRayTraceResult) {
                Direction.Axis axis = ((BlockRayTraceResult) rayTraceResult).getFace().getAxis();
                if (axis == Direction.Axis.X) {
                    x = -motion.x;
                } else if (axis == Direction.Axis.Y) {
                    y = -motion.y;
                } else if (axis == Direction.Axis.Z) {
                    z = -motion.z;
                }
            } else if (rayTraceResult instanceof EntityRayTraceResult) {
                x = rand.nextDouble() * 2 - 1;
                y = rand.nextDouble();
                z = rand.nextDouble() * 2 - 1;
            }
            arrow.shoot(x, y, z, (float) motion.length(), 10);
            world.addEntity(arrow);
        }
    }

    private void doBoneMealThings() {
        BlockPos.getAllInBox((int) (posX + 0.5) - 5, (int) (posY + 0.5) - 5, (int) (posZ + 0.5) - 5, (int) (posX + 0.5) + 5, (int) (posY + 0.5) + 5, (int) (posZ + 0.5) + 5).forEach(pos -> {
            // noinspection deprecation
            if (rand.nextInt(8) == 0 && BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), world, pos)) {
                world.playEvent(2005, pos, 0);
            }
        });
    }

    private void doPotionThings(@Nullable RayTraceResult rayTraceResult) {
        if (!world.isRemote) {
            ItemStack stack = dataManager.get(POTION_STACK);
            Potion potion = PotionUtils.getPotionFromItem(stack);
            List<EffectInstance> list = PotionUtils.getEffectsFromStack(stack);
            boolean isWater = potion == Potions.WATER && list.isEmpty();

            if (rayTraceResult instanceof BlockRayTraceResult && isWater) {
                BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getPos().offset(((BlockRayTraceResult) rayTraceResult).getFace());
                extinguishFires(pos, ((BlockRayTraceResult) rayTraceResult).getFace());

                for (Direction face : Direction.Plane.HORIZONTAL) {
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
                    if (rayTraceResult instanceof EntityRayTraceResult) {
                        hitEntity = ((EntityRayTraceResult) rayTraceResult).getEntity();
                    }
                    applySplash(list, hitEntity);
                }
            }

            // spawn particles
            int eventType = potion.hasInstantEffect() ? 2007 : 2002;
            world.playEvent(eventType, new BlockPos(this), PotionUtils.getColor(stack));
        }
    }

    private void extinguishFires(BlockPos pos, Direction face) {
        if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
            this.world.extinguishFire(null, pos.offset(face), face.getOpposite());
        }
    }

    private void applyWater() {
        AxisAlignedBB axisalignedbb = getBoundingBox().grow(5, 3, 5);
        List<LivingEntity> list = world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb, PotionEntity.WATER_SENSITIVE);
        if (!list.isEmpty()) {
            for (LivingEntity entitylivingbase : list) {
                double distance = getDistanceSq(entitylivingbase);

                if (distance < 16.0D && (entitylivingbase instanceof EndermanEntity || entitylivingbase instanceof BlazeEntity)) {
                    entitylivingbase.attackEntityFrom(DamageSource.DROWN, 1);
                }
            }
        }
    }

    private void applySplash(List<EffectInstance> effectInstances, @Nullable Entity hitEntity) {
        AxisAlignedBB boundingBox = getBoundingBox().grow(4, 2, 4);
        List<LivingEntity> entities = world.getEntitiesWithinAABB(LivingEntity.class, boundingBox);

        for (LivingEntity entity : entities) {
            double distance = getDistanceSq(entity);
            if (!entity.canBeHitWithPotion() || distance >= 16) {
                break;
            }

            double effectMultiplier = 1 - Math.sqrt(distance) / 4;
            if (entity == hitEntity) {
                effectMultiplier = 1;
            }

            for (EffectInstance effectInstance : effectInstances) {
                Effect effect = effectInstance.getPotion();
                if (effect.isInstant()) {
                    effect.affectEntity(this, getShootingEntity(), entity, effectInstance.getAmplifier(), effectMultiplier);
                } else {
                    int duration = (int) (effectMultiplier * (double) effectInstance.getDuration() + 0.5);
                    if (duration > 20) {
                        entity.addPotionEffect(new EffectInstance(effect, duration, effectInstance.getAmplifier(), effectInstance.isAmbient(), effectInstance.doesShowParticles()));
                    }
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(ItemStack stack, Potion potion) {
        AreaEffectCloudEntity effectCloud = new AreaEffectCloudEntity(world, posX, posY, posZ);
        effectCloud.setOwner(getShootingEntity());
        effectCloud.setRadius(3.2F);
        effectCloud.setRadiusOnUse(-0.4F);
        effectCloud.setWaitTime(10);
        effectCloud.setRadiusPerTick(-effectCloud.getRadius() / effectCloud.getDuration());
        effectCloud.setPotion(potion);

        for (EffectInstance effectInstance : PotionUtils.getFullEffectsFromItem(stack)) {
            effectCloud.addEffect(new EffectInstance(effectInstance));
        }

        CompoundNBT compoundNBT = stack.getTag();
        if (compoundNBT != null && compoundNBT.contains("CustomPotionColor", 99)) {
            effectCloud.setColor(compoundNBT.getInt("CustomPotionColor"));
        }

        world.addEntity(effectCloud);
    }

    private void dealFireworkExplosionDamage() {
        int damageMultiplier = 0;
        ListNBT explosions = dataManager.get(FIREWORKS).getList("Explosions", 10);

        if (!explosions.isEmpty()) {
            damageMultiplier = 5 + explosions.size() * 2;
        }

        if (damageMultiplier > 0) {
            Vec3d posVec = new Vec3d(posX, posY, posZ);

            for (LivingEntity entity : world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(5))) {
                if (getDistanceSq(entity) <= 25) {
                    boolean flag = false;
                    for (int i = 0; i < 2; ++i) {
                        RayTraceResult raytraceresult = world.rayTraceBlocks(new RayTraceContext(posVec, new Vec3d(entity.posX, entity.posY + entity.getHeight() * 0.5 * i, entity.posZ), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
                        if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        entity.attackEntityFrom(DamageSource.FIREWORKS, damageMultiplier * (float) Math.sqrt((5 - getDistance(entity)) / 5));
                    }
                }
            }
        }
    }

    private void spawnParticles() {
        if (world.isRemote) {
            if (isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    world.addParticle(ParticleTypes.BUBBLE, posX - getMotion().getX() * 0.25, posY - getMotion().getY() * 0.25, posZ - getMotion().getZ() * 0.25, getMotion().getX(), getMotion().getY(), getMotion().getZ());
                }
            } else {
                if (dataManager.get(IS_FLAMING)) {
                    spawnParticle(ParticleTypes.FLAME, 0.25, 0.6, 0);
                }
                if (dataManager.get(IS_SMOKING)) {
                    for (int i = 0; i < 3; i++) {
                        spawnParticle(ParticleTypes.SMOKE, 0.25, 0.3, 0);
                    }
                    if (ticksExisted % 2 == 0) {
                        spawnParticle(ParticleTypes.LARGE_SMOKE, 0.4, 0.3, 0);
                    }
                }
                if (!dataManager.get(FIREWORKS).isEmpty()) {
                    world.addParticle(ParticleTypes.FIREWORK, posX, posY, posZ, rand.nextGaussian() * 0.05, -getMotion().getY() * 0.5, rand.nextGaussian() * 0.05);
                }
                if (dataManager.get(IS_BONE_MEAL) && ticksExisted % 3 == 0) {
                    spawnParticle(ParticleTypes.HAPPY_VILLAGER, 0.1, 0, 0.02);
                }
                if (dataManager.get(IS_ENDER_PEARL)) {
                    spawnParticle(ParticleTypes.PORTAL, 0.3, 0, 0.08);
                }
                if (!dataManager.get(POTION_STACK).isEmpty()) {
                    int color = PotionUtils.getColor(dataManager.get(POTION_STACK));
                    if (color > 0) {
                        world.addOptionalParticle(ParticleTypes.ENTITY_EFFECT, posX + (rand.nextDouble() - 0.5) * getWidth(), posY + rand.nextDouble() * getHeight(), posZ + (rand.nextDouble() - 0.5) * getWidth(), (color >> 16 & 255) / 255D, (color >> 8 & 255) / 255D, (color & 255) / 255D);
                    }
                }
            }
        }
    }

    private void spawnParticle(IParticleData particle, double spreadMultiplier, double motionMultiplier, double motionSpreadMultiplier) {
        world.addParticle(particle, posX + rand.nextGaussian() * spreadMultiplier, posY + rand.nextGaussian() * spreadMultiplier + 0.5, posZ + rand.nextGaussian() * spreadMultiplier, getMotion().getX() * motionMultiplier + rand.nextGaussian() * motionSpreadMultiplier, getMotion().getY() * motionMultiplier + rand.nextGaussian() * motionSpreadMultiplier, getMotion().getZ() * motionMultiplier + rand.nextGaussian() * motionSpreadMultiplier);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        switch (id) {
            case (17):
                world.makeFireworks(posX, posY, posZ, getMotion().getX(), getMotion().getY(), getMotion().getZ(), dataManager.get(FIREWORKS));
                break;
            case (100):
                for (int j = 0; j < 16; ++j) {
                    float rotationXZ = (float) (rand.nextFloat() * Math.PI * 2);
                    float rotationY = (float) (rand.nextFloat() * Math.PI);
                    float distance = rand.nextFloat() * 0.4F + 0.3F;
                    float x = MathHelper.sin(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float y = MathHelper.cos(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float z = MathHelper.cos(rotationY) * distance;
                    world.addParticle(ParticleTypes.ITEM_SLIME, posX + x, posY + y, posZ + z, 0, 0, 0);
                }
                break;
            case (101):
                for (int j = 0; j < 48; ++j) {
                    float rotationXZ = (float) (rand.nextFloat() * Math.PI * 2);
                    float rotationY = (float) (rand.nextFloat() * Math.PI);
                    float distance = rand.nextFloat() * 0.4F + 0.3F;
                    float x = MathHelper.sin(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float y = MathHelper.cos(rotationXZ) * MathHelper.sin(rotationY) * distance;
                    float z = MathHelper.cos(rotationY) * distance;
                    world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, getBlockState()), posX + x, posY + y, posZ + z, 0, 0, 0);
                }
                break;
            default:
                super.handleStatusUpdate(id);
                break;
        }
    }
}
 */
