package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MissHitResult;
import meteordevelopment.meteorclient.utils.misc.Pool;
import meteordevelopment.meteorclient.utils.misc.Vec3;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1675;
import net.minecraft.class_1680;
import net.minecraft.class_1681;
import net.minecraft.class_1684;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3610;
import net.minecraft.class_3612;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class PearlPredict extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Integer> simulation;
   private final Setting<Boolean> accurate;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final PearlPredict.ProjectileEntitySimulator simulator;
   private final Pool<Vec3> vec3s;
   private final List<PearlPredict.Path> paths;

   public PearlPredict() {
      super(Categories.NewCombat, "Predict-Pearl", "Predicts where pearls will land and shows the path they will take.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.simulation = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("simulation-times")).description("How long to simulate the enderpearl.")).defaultValue(128)).min(10).sliderMin(100).sliderMax(200).min(5).build());
      this.accurate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("accurate")).description("Whether or not to calculate more accurate.")).defaultValue(false)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(0, 255, 100, 35))).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(0, 255, 100, 255))).build());
      this.simulator = new PearlPredict.ProjectileEntitySimulator();
      this.vec3s = new Pool(Vec3::new);
      this.paths = new ArrayList();
   }

   private PearlPredict.Path getEmptyPath() {
      Iterator var1 = this.paths.iterator();

      PearlPredict.Path path;
      do {
         if (!var1.hasNext()) {
            PearlPredict.Path path = new PearlPredict.Path();
            this.paths.add(path);
            return path;
         }

         path = (PearlPredict.Path)var1.next();
      } while(!path.points.isEmpty());

      return path;
   }

   private void calculatePath(class_1297 entity, double tickDelta) {
      Iterator var4 = this.paths.iterator();

      while(var4.hasNext()) {
         PearlPredict.Path path = (PearlPredict.Path)var4.next();
         path.clear();
      }

      this.simulator.set(entity, 1.5D, 0.03D, 0.8D, (Boolean)this.accurate.get(), tickDelta);
      this.getEmptyPath().calculate();
   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      Iterator var2 = this.mc.field_1687.method_18112().iterator();

      while(true) {
         class_1297 entity;
         do {
            if (!var2.hasNext()) {
               return;
            }

            entity = (class_1297)var2.next();
         } while(!(entity instanceof class_1684) && !(entity instanceof class_1680) && !(entity instanceof class_1681));

         this.calculatePath(entity, (double)event.tickDelta);
         Iterator var4 = this.paths.iterator();

         while(var4.hasNext()) {
            PearlPredict.Path path = (PearlPredict.Path)var4.next();
            path.render(event);
         }
      }
   }

   private class ProjectileEntitySimulator {
      private static final class_2339 blockPos = new class_2339();
      private static final class_243 pos3d = new class_243(0.0D, 0.0D, 0.0D);
      private static final class_243 prevPos3d = new class_243(0.0D, 0.0D, 0.0D);
      public final Vec3 pos = new Vec3();
      private final Vec3 velocity = new Vec3();
      private double gravity;
      private double airDrag;
      private double waterDrag;

      public void set(class_1297 entity, double speed, double gravity, double waterDrag, boolean accurate, double tickDelta) {
         this.pos.set(entity, tickDelta);
         this.velocity.set(entity.method_18798()).normalize().multiply(speed);
         if (accurate) {
            class_243 vel = entity.method_18798();
            this.velocity.add(vel.field_1352, entity.method_24828() ? 0.0D : vel.field_1351, vel.field_1350);
         }

         this.gravity = gravity;
         this.airDrag = 0.99D;
         this.waterDrag = waterDrag;
      }

      public class_239 tick() {
         ((IVec3d)prevPos3d).set(this.pos);
         this.pos.add(this.velocity);
         this.velocity.multiply(this.isTouchingWater() ? this.waterDrag : this.airDrag);
         this.velocity.subtract(0.0D, this.gravity, 0.0D);
         if (this.pos.y < 0.0D) {
            return MissHitResult.INSTANCE;
         } else {
            int chunkX = (int)(this.pos.x / 16.0D);
            int chunkZ = (int)(this.pos.z / 16.0D);
            if (!PearlPredict.this.mc.field_1687.method_2935().method_12123(chunkX, chunkZ)) {
               return MissHitResult.INSTANCE;
            } else {
               ((IVec3d)pos3d).set(this.pos);
               class_239 hitResult = this.getCollision();
               return hitResult.method_17783() == class_240.field_1333 ? null : hitResult;
            }
         }
      }

      private boolean isTouchingWater() {
         blockPos.method_10102(this.pos.x, this.pos.y, this.pos.z);
         class_3610 fluidState = PearlPredict.this.mc.field_1687.method_8316(blockPos);
         if (fluidState.method_15772() != class_3612.field_15910 && fluidState.method_15772() != class_3612.field_15909) {
            return false;
         } else {
            return this.pos.y - (double)((int)this.pos.y) <= (double)fluidState.method_20785();
         }
      }

      private class_239 getCollision() {
         class_243 vec3d3 = prevPos3d;
         class_239 hitResult = PearlPredict.this.mc.field_1687.method_17742(new class_3959(vec3d3, pos3d, class_3960.field_17558, this.waterDrag == 0.0D ? class_242.field_1347 : class_242.field_1348, PearlPredict.this.mc.field_1724));
         if (((class_239)hitResult).method_17783() != class_240.field_1333) {
            vec3d3 = ((class_239)hitResult).method_17784();
         }

         class_239 hitResult2 = class_1675.method_18077(PearlPredict.this.mc.field_1687, PearlPredict.this.mc.field_1724, vec3d3, pos3d, (new class_238(this.pos.x, this.pos.y, this.pos.z, this.pos.x, this.pos.y, this.pos.z)).method_18804(PearlPredict.this.mc.field_1724.method_18798()).method_1014(1.0D), (entity) -> {
            return !entity.method_7325() && entity.method_5805() && entity.method_5863();
         });
         if (hitResult2 != null) {
            hitResult = hitResult2;
         }

         return (class_239)hitResult;
      }
   }

   private class Path {
      private final List<Vec3> points = new ArrayList();
      private boolean hitQuad;
      private boolean hitQuadHorizontal;
      private double hitQuadX1;
      private double hitQuadY1;
      private double hitQuadZ1;
      private double hitQuadX2;
      private double hitQuadY2;
      private double hitQuadZ2;
      private class_1297 entity;

      public void clear() {
         Iterator var1 = this.points.iterator();

         while(var1.hasNext()) {
            Vec3 point = (Vec3)var1.next();
            PearlPredict.this.vec3s.free(point);
         }

         this.points.clear();
         this.hitQuad = false;
         this.entity = null;
      }

      public void calculate() {
         this.addPoint();

         for(int i = 0; i < (Integer)PearlPredict.this.simulation.get(); ++i) {
            class_239 result = PearlPredict.this.simulator.tick();
            if (result != null) {
               this.processHitResult(result);
               break;
            }

            this.addPoint();
         }

      }

      private void addPoint() {
         this.points.add(((Vec3)PearlPredict.this.vec3s.get()).set(PearlPredict.this.simulator.pos));
      }

      private void processHitResult(class_239 result) {
         if (result.method_17783() == class_240.field_1332) {
            class_3965 res = (class_3965)result;
            this.hitQuad = true;
            this.hitQuadX1 = res.method_17784().field_1352;
            this.hitQuadY1 = res.method_17784().field_1351;
            this.hitQuadZ1 = res.method_17784().field_1350;
            this.hitQuadX2 = res.method_17784().field_1352;
            this.hitQuadY2 = res.method_17784().field_1351;
            this.hitQuadZ2 = res.method_17784().field_1350;
            if (res.method_17780() != class_2350.field_11036 && res.method_17780() != class_2350.field_11033) {
               if (res.method_17780() != class_2350.field_11043 && res.method_17780() != class_2350.field_11035) {
                  this.hitQuadHorizontal = false;
                  this.hitQuadZ1 -= 0.25D;
                  this.hitQuadY1 -= 0.25D;
                  this.hitQuadZ2 += 0.25D;
                  this.hitQuadY2 += 0.25D;
               } else {
                  this.hitQuadHorizontal = false;
                  this.hitQuadX1 -= 0.25D;
                  this.hitQuadY1 -= 0.25D;
                  this.hitQuadX2 += 0.25D;
                  this.hitQuadY2 += 0.25D;
               }
            } else {
               this.hitQuadHorizontal = true;
               this.hitQuadX1 -= 0.25D;
               this.hitQuadZ1 -= 0.25D;
               this.hitQuadX2 += 0.25D;
               this.hitQuadZ2 += 0.25D;
            }

            this.points.add(((Vec3)PearlPredict.this.vec3s.get()).set(result.method_17784()));
         } else if (result.method_17783() == class_240.field_1331) {
            this.entity = ((class_3966)result).method_17782();
            this.points.add(((Vec3)PearlPredict.this.vec3s.get()).set(result.method_17784()).add(0.0D, (double)(this.entity.method_17682() / 2.0F), 0.0D));
         }

      }

      public void render(Render3DEvent event) {
         Vec3 lastPoint = null;

         Vec3 point;
         for(Iterator var3 = this.points.iterator(); var3.hasNext(); lastPoint = point) {
            point = (Vec3)var3.next();
            if (lastPoint != null) {
               event.renderer.line(lastPoint.x, lastPoint.y, lastPoint.z, point.x, point.y, point.z, (Color)PearlPredict.this.lineColor.get());
            }
         }

         if (this.hitQuad) {
            if (this.hitQuadHorizontal) {
               event.renderer.sideHorizontal(this.hitQuadX1, this.hitQuadY1, this.hitQuadZ1, this.hitQuadX1 + 0.5D, this.hitQuadZ1 + 0.5D, (Color)PearlPredict.this.sideColor.get(), (Color)PearlPredict.this.lineColor.get(), (ShapeMode)PearlPredict.this.shapeMode.get());
            } else {
               event.renderer.sideVertical(this.hitQuadX1, this.hitQuadY1, this.hitQuadZ1, this.hitQuadX2, this.hitQuadY2, this.hitQuadZ2, (Color)PearlPredict.this.sideColor.get(), (Color)PearlPredict.this.lineColor.get(), (ShapeMode)PearlPredict.this.shapeMode.get());
            }
         }

         if (this.entity != null) {
            double x = (this.entity.method_23317() - this.entity.field_6014) * (double)event.tickDelta;
            double y = (this.entity.method_23318() - this.entity.field_6036) * (double)event.tickDelta;
            double z = (this.entity.method_23321() - this.entity.field_5969) * (double)event.tickDelta;
            class_238 box = this.entity.method_5829();
            event.renderer.box(x + box.field_1323, y + box.field_1322, z + box.field_1321, x + box.field_1320, y + box.field_1325, z + box.field_1324, (Color)PearlPredict.this.sideColor.get(), (Color)PearlPredict.this.lineColor.get(), (ShapeMode)PearlPredict.this.shapeMode.get(), 0);
         }

      }
   }
}
