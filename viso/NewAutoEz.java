package meteordevelopment.meteorclient.systems.modules.viso;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.events.entity.player.AttackEntityEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1511;
import net.minecraft.class_1657;
import net.minecraft.class_1802;
import net.minecraft.class_2663;

public class NewAutoEz extends Module {
   boolean p = true;
   private final SettingGroup sgGeneral;
   private final Setting<NewAutoEz.Mode> b;
   private final Setting<Integer> minArmor;
   private final Setting<Boolean> ignoreFriends;
   Setting<List<String>> killMessages;
   Map<Integer, Integer> players;

   public NewAutoEz() {
      super(Categories.NewCombat, "Auto-GG", "");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.b = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("Mode")).description("The mode.")).defaultValue(NewAutoEz.Mode.Message)).build());
      this.minArmor = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("min-armor")).description("Minimum number of armor elements.")).defaultValue(2)).min(0).max(4).sliderMin(0).sliderMax(4).build());
      this.ignoreFriends = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-friends")).defaultValue(true)).build());
      this.killMessages = this.sgGeneral.add(((StringListSetting.Builder)((StringListSetting.Builder)((StringListSetting.Builder)(new StringListSetting.Builder()).name("messages")).defaultValue(Arrays.asList("GG {player}, Viso Client On Top"))).visible(() -> {
         return !this.p;
      })).build());
      this.players = new HashMap();
   }

   public void onActivate() {
      this.players.clear();
   }

   private boolean checkArmor(class_1657 p) {
      int armor = 0;
      class_1304[] var3 = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         class_1304 a = var3[var5];
         if (p.method_6118(a).method_7909() != class_1802.field_8162) {
            ++armor;
         }
      }

      return armor < (Integer)this.minArmor.get();
   }

   private boolean checkFriend(class_1657 p) {
      return (Boolean)this.ignoreFriends.get() && Friends.get().get(p.method_5477().method_10851()) != null;
   }

   private void add(int a) {
      if (this.players.get(a) == null) {
         this.players.put(a, 0);
      } else {
         this.players.put(a, (Integer)this.players.get(a));
      }

   }

   @EventHandler
   private void AttackEntity(AttackEntityEvent e) {
      if (e.entity instanceof class_1511) {
         this.mc.field_1687.method_18456().forEach((p) -> {
            if (this.checkTarget(p) && p.method_5739(e.entity) < 8.0F) {
               this.add(p.method_5628());
            }

         });
      } else if (e.entity instanceof class_1657 && this.checkTarget(e.entity)) {
         this.add(e.entity.method_5628());
      }

   }

   @EventHandler
   private void PacketEvent(PacketEvent.Receive e) {
      if (e.packet instanceof class_2663) {
         class_2663 p = (class_2663)e.packet;
         if (p.method_11469(this.mc.field_1687) instanceof class_1657 && this.checkTarget(p.method_11469(this.mc.field_1687)) && this.players.containsKey(p.method_11469(this.mc.field_1687).method_5628())) {
            if (p.method_11470() == 3) {
               this.ezz(p.method_11469(this.mc.field_1687));
            }

            if (p.method_11470() == 35) {
               int id = p.method_11469(this.mc.field_1687).method_5628();
               if (this.players.get(id) == null) {
                  this.players.put(id, 1);
               } else {
                  this.players.put(id, (Integer)this.players.get(id) + 1);
               }
            }
         }
      }

   }

   private boolean checkTarget(class_1297 a) {
      class_1657 p = (class_1657)a;
      return !p.method_7325() && !p.method_7337() && !p.method_5655() && !this.mc.field_1724.equals(p) && !this.checkArmor(p) && !this.checkFriend(p);
   }

   private void ezz(class_1297 e) {
      int id = e.method_5628();
      if (this.b.get() == NewAutoEz.Mode.Message) {
         if ((Integer)this.players.get(id) == 0 && this.mc.field_1724.method_5739(e) < 8.0F) {
            this.mc.field_1724.method_3142(((String)((List)this.killMessages.get()).get(Utils.random(0, ((List)this.killMessages.get()).size()))).replace("{player}", e.method_5477().getString()));
         } else if ((Integer)this.players.get(id) != 0 && this.mc.field_1724.method_5739(e) < 8.0F) {
            this.mc.field_1724.method_3142(((String)((List)this.killMessages.get()).get(Utils.random(0, ((List)this.killMessages.get()).size()))).replace("{player}", e.method_5477().getString()));
         }

         this.players.remove(id);
      } else if (this.b.get() == NewAutoEz.Mode.Client) {
         if ((Integer)this.players.get(id) == 0 && this.mc.field_1724.method_5739(e) < 8.0F) {
            ChatUtils.info(((String)((List)this.killMessages.get()).get(Utils.random(0, ((List)this.killMessages.get()).size()))).replace("{player}", e.method_5477().getString()));
         } else if ((Integer)this.players.get(id) != 0 && this.mc.field_1724.method_5739(e) < 8.0F) {
            ChatUtils.info(((String)((List)this.killMessages.get()).get(Utils.random(0, ((List)this.killMessages.get()).size()))).replace("{player}", e.method_5477().getString()));
         }

         this.players.remove(id);
      }

   }

   public static enum Mode {
      Client,
      Message;

      // $FF: synthetic method
      private static NewAutoEz.Mode[] $values() {
         return new NewAutoEz.Mode[]{Client, Message};
      }
   }
}
