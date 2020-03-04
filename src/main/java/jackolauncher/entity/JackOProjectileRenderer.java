package jackolauncher.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class JackOProjectileRenderer extends EntityRenderer<JackOProjectileEntity> {

    public JackOProjectileRenderer(EntityRenderDispatcher rendererManager) {
        super(rendererManager);
        field_4673 = 0.5F;
    }

    @Override
    public void render(JackOProjectileEntity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();

        GlStateManager.translatef((float) x, (float) y + 0.5F, (float) z);
        float rotation = 20 * (entity.ticksInAir + entity.randomRotationOffset + partialTicks);
        GlStateManager.rotatef(rotation / 2.5F, 0.7071F, 0, 0.7071F);
        GlStateManager.rotatef(rotation, 0, 1, 0);
        GlStateManager.translatef(-0.5F, -0.5F, +0.5F);

        bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        BlockRenderManager blockrendererdispatcher = MinecraftClient.getInstance().getBlockRenderManager();
        blockrendererdispatcher.getModelRenderer().render(blockrendererdispatcher.getModel(entity.getBlockState()), entity.getBlockState(), 1, false);

        GlStateManager.popMatrix();
        super.render(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected Identifier getTexture(JackOProjectileEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}
