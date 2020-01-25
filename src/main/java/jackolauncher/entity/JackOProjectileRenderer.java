package jackolauncher.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

/*
@OnlyIn(Dist.CLIENT)
public class JackOProjectileRenderer extends EntityRenderer<JackOProjectileEntity> {

    public JackOProjectileRenderer(EntityRendererManager rendererManager) {
        super(rendererManager);
        shadowSize = 0.5F;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void doRender(JackOProjectileEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.translatef((float) x, (float) y + 0.5F, (float) z);
        float rotation = 20 * (entity.ticksInAir + entity.randomRotationOffset + partialTicks);
        GlStateManager.rotatef(rotation / 2.5F, 0.7071F, 0, 0.7071F);
        GlStateManager.rotatef(rotation, 0, 1, 0);
        GlStateManager.translatef(-0.5F, -0.5F, +0.5F);

        bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        blockrendererdispatcher.getBlockModelRenderer().renderModelBrightness(blockrendererdispatcher.getModelForState(entity.getBlockState()), entity.getBlockState(), 1, false);

        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected ResourceLocation getEntityTexture(JackOProjectileEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
 */
