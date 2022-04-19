package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.BPlusEntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_2338;

public class BurrowESP extends Module {
   private final SettingGroup sgRender;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<Boolean> renderWebbed;
   private final Setting<SettingColor> WebsideColor;
   private final Setting<SettingColor> WeblineColor;
   public class_2338 target;
   public boolean isTargetWebbed;
   public boolean isTargetBurrowed;

   public BurrowESP() {
      super(Categories.NewCombat, "Burrow-ESP", "Displays if the closest target to you is burrowed / webbed.");
      this.sgRender = this.settings.createGroup("_Render_");
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the rendering.")).defaultValue(new SettingColor(230, 0, 255, 5))).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the rendering.")).defaultValue(new SettingColor(250, 0, 255, 255))).build());
      this.renderWebbed = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Render")).description("Will render if the target is webbed")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      ColorSetting.Builder var10002 = (ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the rendering for webs.")).defaultValue(new SettingColor(240, 250, 65, 35));
      Setting var10003 = this.renderWebbed;
      Objects.requireNonNull(var10003);
      this.WebsideColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
      var10001 = this.sgRender;
      var10002 = (ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the rendering for webs.")).defaultValue(new SettingColor(0, 0, 0, 0));
      var10003 = this.renderWebbed;
      Objects.requireNonNull(var10003);
      this.WeblineColor = var10001.add(((ColorSetting.Builder)var10002.visible(var10003::get)).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      class_1657 targetEntity = TargetUtils.getPlayerTarget((double)(this.mc.field_1761.method_2904() + 2.0F), SortPriority.LowestDistance);
      if (TargetUtils.isBadTarget(targetEntity, (double)(this.mc.field_1761.method_2904() + 2.0F))) {
         this.target = null;
      } else if ((Boolean)this.renderWebbed.get() && BPlusEntityUtils.isWebbed(targetEntity)) {
         this.target = targetEntity.method_24515();
      } else if (BPlusEntityUtils.isBurrowed(targetEntity)) {
         this.target = targetEntity.method_24515();
      } else {
         this.target = null;
      }

      this.isTargetWebbed = this.target != null && BPlusEntityUtils.isWebbed(targetEntity);
      this.isTargetBurrowed = this.target != null && BPlusEntityUtils.isBurrowed(targetEntity);
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.target != null) {
         if (this.isTargetWebbed) {
            event.renderer.box((class_2338)this.target, (Color)this.WebsideColor.get(), (Color)this.WeblineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         } else if (this.isTargetBurrowed) {
            event.renderer.box((class_2338)this.target, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
         }

      }
   }
}
