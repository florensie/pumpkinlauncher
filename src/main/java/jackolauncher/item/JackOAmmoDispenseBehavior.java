package jackolauncher.item;

//import jackolauncher.entity.JackOProjectileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.Position;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

/*
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JackOAmmoDispenseBehavior extends ProjectileDispenseBehavior {

    @Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
        World world = blockSource.getWorld();
        IPosition position = DispenserBlock.getDispensePosition(blockSource);
        Direction direction = blockSource.getBlockState().get(DispenserBlock.FACING);
        IProjectile projectile = getProjectileEntity(world, new Position(position.getX() + direction.getXOffset() * 0.5, position.getY() - 0.5 + direction.getYOffset() * 0.5, position.getZ() + direction.getZOffset() * 0.5), stack);
        projectile.shoot(direction.getXOffset(), direction.getYOffset(), direction.getZOffset(), getProjectileVelocity(), getProjectileInaccuracy());
        world.addEntity((Entity) projectile);
        stack.shrink(1);
        return stack;
    }

    @Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
        return new JackOProjectileEntity(world, position.getX(), position.getY(), position.getZ(), JackOAmmoHelper.getAmmoProperties(stack));
    }

    @Override
    protected float getProjectileVelocity() {
        return 1.3F;
    }
}
*/