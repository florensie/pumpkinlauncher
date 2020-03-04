package jackolauncher;

import jackolauncher.item.JackOLauncherItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Arm;

@SuppressWarnings("unused")
public class ClientEventHandler {

/*    @Environment(EnvType.CLIENT)
    public static void setJackOLauncherArmPose(RenderLivingEvent.Pre event) {
        boolean isHoldingOffHand = event.getEntity().getOffHandStack().getItem() instanceof JackOLauncherItem;
        boolean isHoldingMainHand = event.getEntity().getMainHandStack().getItem() instanceof JackOLauncherItem;
        if ((isHoldingMainHand && MinecraftClient.getInstance().options.mainArm == Arm.field_6183) || (isHoldingOffHand && MinecraftClient.getInstance().options.mainArm == Arm.field_6182)) {
            ((BipedEntityModel) event.getRenderer().getModel()).rightArmPose = BipedEntityModel.ArmPose.field_3408;
        } else if ((isHoldingMainHand && MinecraftClient.getInstance().options.mainArm == Arm.field_6182) || (isHoldingOffHand && MinecraftClient.getInstance().options.mainArm == Arm.field_6183)) {
            ((BipedEntityModel) event.getRenderer().getModel()).leftArmPose = BipedEntityModel.ArmPose.field_3408;
        }
    }*/
}
