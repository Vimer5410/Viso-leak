package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_243;

public class TunnelESP extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<SettingColor> color;
   private final Setting<Integer> delay;
   private final Setting<Integer> range;
   private final List<class_2338> poses;
   public class_243 prevPos;
   private double[] rPos;

   public TunnelESP() {
      super(Categories.NewCombat, "Tunnel-ESP", "Tunnel ESP.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.color = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("color")).description("")).defaultValue(new SettingColor(255, 0, 190, 255))).build());
      this.delay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("delay")).description("delay")).defaultValue(1)).min(0).sliderMax(20).build());
      this.range = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("range")).description("Range")).defaultValue(50)).min(0).sliderMax(150).build());
      this.poses = new ArrayList();
   }

   @EventHandler
   public void onTick(TickEvent.Pre e) {
      if (this.mc.field_1724.field_6012 % (Integer)this.delay.get() == 0) {
         this.update((Integer)this.range.get());
      }

   }

   public void update(int range) {
      this.poses.clear();
      class_2338 player = this.mc.field_1724.method_24515();
      this.prevPos = this.mc.field_1724.method_19538();

      for(int y = -Math.min(range, player.method_10264()); y < Math.min(range, 255 - player.method_10264()); ++y) {
         for(int x = -range; x < range; ++x) {
            for(int z = -range; z < range; ++z) {
               class_2338 pos = player.method_10069(x, y, z);
               if (this.mc.field_1687.method_8320(pos).method_26215() && this.mc.field_1687.method_8320(pos.method_10086(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10087(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10086(2)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10076(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10077(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10086(1).method_10076(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10086(1).method_10077(1)).method_26215() && this.mc.field_1687.method_8320(pos.method_10088(1)).method_26215() && this.mc.field_1687.method_8320(pos.method_10088(1).method_10086(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10088(1).method_10087(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10088(1).method_10086(2)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10088(1).method_10076(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10088(1).method_10077(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10088(1).method_10086(1).method_10076(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10088(1).method_10086(1).method_10077(1)).method_26215() || this.mc.field_1687.method_8320(pos).method_26215() && this.mc.field_1687.method_8320(pos.method_10086(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10087(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10086(2)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10088(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10089(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10086(1).method_10088(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10086(1).method_10089(1)).method_26215() && this.mc.field_1687.method_8320(pos.method_10076(1)).method_26215() && this.mc.field_1687.method_8320(pos.method_10076(1).method_10086(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10076(1).method_10087(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10076(1).method_10086(2)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10076(1).method_10088(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10076(1).method_10089(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10076(1).method_10086(1).method_10088(1)).method_26215() && !this.mc.field_1687.method_8320(pos.method_10076(1).method_10086(1).method_10089(1)).method_26215()) {
                  this.poses.add(pos);
               }
            }
         }
      }

   }

   @EventHandler
   public void onRender(Render3DEvent a) {
      Iterator var2 = this.poses.iterator();

      while(var2.hasNext()) {
         class_2338 p = (class_2338)var2.next();
         a.renderer.box((class_2338)p, (Color)this.color.get(), (Color)this.color.get(), ShapeMode.Lines, 0);
         a.renderer.box((class_2338)p.method_10084(), (Color)this.color.get(), (Color)this.color.get(), ShapeMode.Lines, 0);
      }

   }
}
