package meteordevelopment.meteorclient.systems.modules.viso;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2346;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2480;
import net.minecraft.class_3965;

public class ScaffoldPlus extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<ScaffoldPlus.Dir> direction;
   private final Setting<Integer> shift;
   private final Setting<Integer> radius;
   private final Setting<Boolean> center;
   private final Setting<Boolean> fall;

   public ScaffoldPlus() {
      super(Categories.NewCombat, "Scaffold-V2", "");
      this.sgGeneral = this.settings.createGroup("_General_");
      this.direction = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Direction")).defaultValue(ScaffoldPlus.Dir.DOWN)).build());
      this.shift = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("shift")).description("Shift your scaffold. (Up / Down)")).defaultValue(0)).min(-4).max(6).sliderMin(-4).sliderMax(6).build());
      this.radius = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("radius")).description("The radius of your scaffold.")).defaultValue(0)).min(0).sliderMax(6).build());
      this.center = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Center first")).description("Place center block first")).defaultValue(true)).build());
      this.fall = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Allow Falling blocks")).defaultValue(true)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      if (this.mc.field_1724.method_6047().method_7909() instanceof class_1747) {
         class_2248 block = ((class_1747)this.mc.field_1724.method_6047().method_7909()).method_7711();
         if (!(block instanceof class_2480)) {
            if ((Boolean)this.fall.get() || !(block instanceof class_2346)) {
               int px = this.mc.field_1724.method_24515().method_10263();
               int py = this.mc.field_1724.method_24515().method_10264() - 1 + (Integer)this.shift.get();
               int pz = this.mc.field_1724.method_24515().method_10260();

               for(int x = px - (Integer)this.radius.get(); x <= px + (Integer)this.radius.get(); ++x) {
                  for(int z = pz - (Integer)this.radius.get(); z <= pz + (Integer)this.radius.get(); ++z) {
                     if ((Boolean)this.center.get() && this.place(new class_243((double)px, (double)py, (double)pz))) {
                        return;
                     }

                     if (this.distance((double)x, (double)py, (double)z) <= (double)this.mc.field_1761.method_2904() && this.place(new class_243((double)x, (double)py, (double)z))) {
                        return;
                     }
                  }
               }

            }
         }
      }
   }

   private boolean place(class_243 pos) {
      class_2338 bpos = new class_2338(pos);
      if (!BlockUtils.canPlace(bpos, true)) {
         return false;
      } else {
         this.mc.field_1761.method_2896(this.mc.field_1724, this.mc.field_1687, class_1268.field_5808, new class_3965(pos, class_2350.valueOf(((ScaffoldPlus.Dir)this.direction.get()).toString()), bpos, false));
         return true;
      }
   }

   private double distance(double x, double y, double z) {
      double d = (double)this.mc.field_1724.method_24515().method_10263() - x;
      double e = (double)this.mc.field_1724.method_24515().method_10264() - y;
      double f = (double)this.mc.field_1724.method_24515().method_10260() - z;
      return d;
   }

   public static enum Dir {
      UP,
      DOWN;

      // $FF: synthetic method
      private static ScaffoldPlus.Dir[] $values() {
         return new ScaffoldPlus.Dir[]{UP, DOWN};
      }
   }
}
