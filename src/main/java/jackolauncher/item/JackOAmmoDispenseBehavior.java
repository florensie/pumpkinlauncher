package jackolauncher.item;

import jackolauncher.entity.JackOProjectileEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.PositionImpl;
import net.minecraft.world.World;

public class JackOAmmoDispenseBehavior extends ProjectileDispenserBehavior {

    @Override
    public ItemStack dispenseSilently(BlockPointer blockSource, ItemStack stack) {
        World world = blockSource.getWorld();
        Position position = DispenserBlock.getOutputLocation(blockSource);
        Direction direction = blockSource.getBlockState().get(DispenserBlock.FACING);
        Projectile projectile = createProjectile(world, new PositionImpl(position.getX() + direction.getOffsetX() * 0.5, position.getY() - 0.5 + direction.getOffsetY() * 0.5, position.getZ() + direction.getOffsetZ() * 0.5), stack);
        projectile.setVelocity(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ(), getForce(), getVariation());
        world.spawnEntity((Entity) projectile);
        stack.decrement(1);
        return stack;
    }

    @Override
    protected Projectile createProjectile(World world, Position position, ItemStack stack) {
        return new JackOProjectileEntity(world, position.getX(), position.getY(), position.getZ(), JackOAmmoHelper.getAmmoProperties(stack));
    }

    @Override
    protected float getForce() {
        return 1.3F;
    }
}
