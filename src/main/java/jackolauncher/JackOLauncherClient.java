package jackolauncher;

import jackolauncher.entity.JackOProjectileEntity;
import jackolauncher.entity.JackOProjectileRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.EntityRendererRegistry;

@SuppressWarnings("unused")
public class JackOLauncherClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(JackOProjectileEntity.class, (entityRenderDispatcher, context) -> new JackOProjectileRenderer(entityRenderDispatcher));
    }
}
