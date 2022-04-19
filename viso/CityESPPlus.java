package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.Objects;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
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
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_638;

public class CityESPPlus extends Module {
   private final SettingGroup sgRender;
   private final Setting<Double> renderExtender;
   private final Setting<Boolean> PrioBurrowed;
   private final Setting<Boolean> NoRenderSurrounded;
   private final Setting<Boolean> AvoidSelf;
   private final Setting<Boolean> LastResort;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   public class_2338 target;

   public CityESPPlus() {
      super(Categories.NewCombat, "CityEsp-V2", "Displays more blocks that can be broken in order to city another player.");
      this.sgRender = this.settings.createGroup("_Render_");
      this.renderExtender = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("Render-extender")).description("The distance which it will increase the normal mc interaction manager by.")).defaultValue(2.0D).min(0.0D).sliderMax(6.0D).build());
      this.PrioBurrowed = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Prioritise Burrow")).description("Will prioritise rendering the burrow block.")).defaultValue(true)).build());
      this.NoRenderSurrounded = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Not Surrounded")).description("Will not render if the target is not surrounded.")).defaultValue(true)).build());
      this.AvoidSelf = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Avoid Self")).description("Will avoid targetting self surround.")).defaultValue(true)).build());
      SettingGroup var10001 = this.sgRender;
      BoolSetting.Builder var10002 = (BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("Last Resort")).description("Will try to target your own surround as final option.")).defaultValue(true);
      Setting var10003 = this.AvoidSelf;
      Objects.requireNonNull(var10003);
      this.LastResort = var10001.add(((BoolSetting.Builder)var10002.visible(var10003::get)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color of the rendering.")).defaultValue(new SettingColor(230, 0, 255, 5))).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color of the rendering.")).defaultValue(new SettingColor(250, 0, 255, 255))).build());
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      class_1657 targetEntity = TargetUtils.getPlayerTarget((double)this.mc.field_1761.method_2904() + (Double)this.renderExtender.get(), SortPriority.LowestDistance);
      if (TargetUtils.isBadTarget(targetEntity, (double)(this.mc.field_1761.method_2904() + 2.0F))) {
         this.target = null;
      } else if ((Boolean)this.PrioBurrowed.get() && BPlusEntityUtils.isBurrowed(targetEntity) && !((class_638)Objects.requireNonNull(this.mc.field_1687)).method_8320(targetEntity.method_24515()).method_27852(class_2246.field_9987)) {
         this.target = targetEntity.method_24515();
      } else if ((Boolean)this.NoRenderSurrounded.get() && !BPlusEntityUtils.isSurrounded(targetEntity)) {
         this.target = null;
      } else if ((Boolean)this.AvoidSelf.get()) {
         this.target = BPlusEntityUtils.getTargetBlock(targetEntity);
         if (this.target == null && (Boolean)this.LastResort.get()) {
            this.target = BPlusEntityUtils.getCityBlock(targetEntity);
         }
      } else {
         this.target = BPlusEntityUtils.getCityBlock(targetEntity);
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.target != null) {
         event.renderer.box((class_2338)this.target, (Color)this.sideColor.get(), (Color)this.lineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
      }
   }
}
