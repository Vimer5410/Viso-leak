package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.ArrayList;
import java.util.Iterator;
import meteordevelopment.meteorclient.mixin.AbstractBlockAccessor;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerManager;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_742;

public class CityUtils {
   private static final class_310 mc = class_310.method_1551();
   private static final class_2338[] surround = new class_2338[]{new class_2338(0, 0, -1), new class_2338(1, 0, 0), new class_2338(0, 0, 1), new class_2338(-1, 0, 0)};

   public static class_1657 getPlayerTarget(double range) {
      if (mc.field_1724.method_29504()) {
         return null;
      } else {
         class_1657 closestTarget = null;
         Iterator var3 = mc.field_1687.method_18456().iterator();

         while(var3.hasNext()) {
            class_742 abstractClientPlayerEntity = (class_742)var3.next();
            if (abstractClientPlayerEntity != mc.field_1724 && !abstractClientPlayerEntity.method_29504() && Friends.get().shouldAttack(abstractClientPlayerEntity) && !((double)mc.field_1724.method_5739(abstractClientPlayerEntity) > range)) {
               if (closestTarget == null) {
                  closestTarget = abstractClientPlayerEntity;
               } else if (mc.field_1724.method_5739(abstractClientPlayerEntity) < mc.field_1724.method_5739((class_1297)closestTarget)) {
                  closestTarget = abstractClientPlayerEntity;
               }
            }
         }

         if (closestTarget == null) {
            var3 = FakePlayerManager.getPlayers().iterator();

            while(var3.hasNext()) {
               FakePlayerEntity target = (FakePlayerEntity)var3.next();
               if (!target.method_29504() && Friends.get().shouldAttack(target) && !((double)mc.field_1724.method_5739(target) > range)) {
                  if (closestTarget == null) {
                     closestTarget = target;
                  } else if (mc.field_1724.method_5739(target) < mc.field_1724.method_5739((class_1297)closestTarget)) {
                     closestTarget = target;
                  }
               }
            }
         }

         return (class_1657)closestTarget;
      }
   }

   public static class_2338 getTargetBlock(class_1657 target) {
      class_2338 finalPos = null;
      ArrayList<class_2338> positions = getTargetSurround(target);
      ArrayList<class_2338> myPositions = getTargetSurround(mc.field_1724);
      if (positions == null) {
         return null;
      } else {
         Iterator var4 = positions.iterator();

         while(true) {
            class_2338 pos;
            do {
               if (!var4.hasNext()) {
                  return finalPos;
               }

               pos = (class_2338)var4.next();
            } while(myPositions != null && !myPositions.isEmpty() && myPositions.contains(pos));

            if (finalPos == null) {
               finalPos = pos;
            } else if (mc.field_1724.method_5707(Utils.vec3d(pos)) < mc.field_1724.method_5707(Utils.vec3d(finalPos))) {
               finalPos = pos;
            }
         }
      }
   }

   private static ArrayList<class_2338> getTargetSurround(class_1657 player) {
      ArrayList<class_2338> positions = new ArrayList();
      boolean isAir = false;

      for(int i = 0; i < 4; ++i) {
         if (player != null) {
            class_2338 obbySurround = getSurround(player, surround[i]);
            if (obbySurround != null) {
               assert mc.field_1687 != null;

               if (mc.field_1687.method_8320(obbySurround) != null) {
                  if (!((AbstractBlockAccessor)mc.field_1687.method_8320(obbySurround).method_26204()).isCollidable()) {
                     isAir = true;
                  }

                  if (mc.field_1687.method_8320(obbySurround).method_26204() == class_2246.field_10540) {
                     positions.add(obbySurround);
                  }
               }
            }
         }
      }

      if (isAir) {
         return null;
      } else {
         return positions;
      }
   }

   public static class_2338 getSurround(class_1297 entity, class_2338 toAdd) {
      class_243 v = entity.method_19538();
      return toAdd == null ? new class_2338(v.field_1352, v.field_1351, v.field_1350) : (new class_2338(v.field_1352, v.field_1351, v.field_1350)).method_10081(toAdd);
   }
}
