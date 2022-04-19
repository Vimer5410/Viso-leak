package meteordevelopment.meteorclient.systems.modules.viso;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.MinecraftClientAccessor;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1774;
import net.minecraft.class_1779;

public class FastUsePlus extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<FastUsePlus.Item> itemChoose;

   public FastUsePlus() {
      super(Categories.NewCombat, "FastUse-V2", "fast throws away");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.itemChoose = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("item")).description(".")).defaultValue(FastUsePlus.Item.Exp)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      switch((FastUsePlus.Item)this.itemChoose.get()) {
      case All:
         this.setClickDelay();
         break;
      case Exp:
         assert this.mc.field_1724 != null;

         if (this.mc.field_1724.method_6047().method_7909() instanceof class_1779) {
            this.setClickDelay();
         }
         break;
      case Crystal:
         assert this.mc.field_1724 != null;

         if (this.mc.field_1724.method_6047().method_7909() instanceof class_1774) {
            this.setClickDelay();
         }
         break;
      case ExpAndCrystal:
         assert this.mc.field_1724 != null;

         if (this.mc.field_1724.method_6047().method_7909() instanceof class_1774 || this.mc.field_1724.method_6047().method_7909() instanceof class_1779) {
            this.setClickDelay();
         }
      }

   }

   private void setClickDelay() {
      ((MinecraftClientAccessor)this.mc).setItemUseCooldown(0);
   }

   public static enum Item {
      All,
      Exp,
      Crystal,
      ExpAndCrystal;

      // $FF: synthetic method
      private static FastUsePlus.Item[] $values() {
         return new FastUsePlus.Item[]{All, Exp, Crystal, ExpAndCrystal};
      }
   }
}
