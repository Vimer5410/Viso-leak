package meteordevelopment.meteorclient.systems.modules.viso;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_156;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3965;

public class blockrenderer extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<blockrenderer.coolList> coolMode;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> lineColor2;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> sideColor2;

   public blockrenderer() {
      super(Categories.NewCombat, "Block-Selection-V2", "cool block renderer.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.coolMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Render-mode")).defaultValue(blockrenderer.coolList.lines)).build());
      this.lineColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color-1")).description("")).defaultValue(new SettingColor(255, 0, 190, 255))).build());
      this.lineColor2 = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color-2")).description("")).defaultValue(new SettingColor(130, 5, 185, 255))).build());
      this.sideColor = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("")).defaultValue(new SettingColor(255, 0, 190, 90))).build());
      this.sideColor2 = this.sgGeneral.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color-2")).description("")).defaultValue(new SettingColor(130, 5, 185, 90))).build());
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      if (this.mc.field_1765 != null) {
         class_239 var3 = this.mc.field_1765;
         if (var3 instanceof class_3965) {
            class_3965 result = (class_3965)var3;
            class_2338 pos = result.method_17777();
            class_2680 var4 = this.mc.field_1687.method_8320(pos);
            class_265 var5 = var4.method_26218(this.mc.field_1687, pos);
            if (var5.method_1110()) {
               return;
            }

            this.render(event, pos);
            return;
         }
      }

   }

   private void render(Render3DEvent event, class_2338 pos) {
      if (this.coolMode.get() == blockrenderer.coolList.lines) {
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor.get(), (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.02D, (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.lineColor.get(), (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor.get(), (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.98D, (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.lineColor.get(), (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor.get(), (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263() + 0.02D, (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor.get(), (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263() + 0.98D, (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.98D, (double)pos.method_10260(), (Color)this.lineColor.get(), (Color)this.lineColor.get());
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263(), (double)pos.method_10264() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor.get());
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor.get());
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor.get());
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get(), (Color)this.lineColor.get());
         event.renderer.quadHorizontal((double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)pos.method_10263() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.02D, (double)pos.method_10260(), (Color)this.lineColor2.get(), (Color)this.lineColor2.get());
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.02D, (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)pos.method_10264() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineColor2.get(), (Color)this.lineColor2.get());
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineColor2.get(), (Color)this.lineColor2.get());
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)pos.method_10260() + 0.98D, (Color)this.lineColor2.get());
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.02D, (double)(pos.method_10260() + 1), (Color)this.lineColor2.get(), (Color)this.lineColor2.get());
         event.renderer.quadHorizontal((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263() + 0.98D, (double)(pos.method_10260() + 1), (Color)this.lineColor2.get());
      }

      if (this.coolMode.get() == blockrenderer.coolList.sides) {
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.sideColor.get(), (Color)this.sideColor2.get());
         event.renderer.gradientQuadVertical((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor.get(), (Color)this.sideColor2.get());
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (Color)this.sideColor.get(), (Color)this.sideColor2.get());
         event.renderer.gradientQuadVertical((double)(pos.method_10263() + 1), (double)pos.method_10264(), (double)(pos.method_10260() + 1), (double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor.get(), (Color)this.sideColor2.get());
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)(pos.method_10264() + 1), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor.get());
         event.renderer.quadHorizontal((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10260() + 1), (Color)this.sideColor2.get());
      }

   }

   public static String bedtrap() {
      StringBuilder stringBuilder = new StringBuilder();
      String string = "68747470733a2f2f646973636f72642e67672f4e4d635363537a785377";

      for(int i = 0; i < string.length(); i += 2) {
         String string2 = string.substring(i, i + 2);
         int n = Integer.parseInt(string2, 16);
         stringBuilder.append((char)n);
      }

      return String.valueOf(stringBuilder);
   }

   public WWidget getWidget(GuiTheme theme) {
      WButton help = theme.button("Join discord!");
      help.action = () -> {
         class_156.method_668().method_670(bedtrap());
      };
      WButton git = theme.button("github here");
      git.action = () -> {
         class_156.method_668().method_670("https://github.com/Kiriyaga7615/karasic");
      };
      return help;
   }

   public static enum coolList {
      lines,
      sides;

      // $FF: synthetic method
      private static blockrenderer.coolList[] $values() {
         return new blockrenderer.coolList[]{lines, sides};
      }
   }
}
