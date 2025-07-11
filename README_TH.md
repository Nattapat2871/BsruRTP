# 🔌 ปลั๊กอิน bsruRTP

<div align="center">
  
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![GitHub Repo stars](https://img.shields.io/github/stars/Nattapat2871/BsruRTP?style=flat-square)](https://github.com/Nattapat2871/BsruRTP/stargazers)
![Visitor Badge](https://api.visitorbadge.io/api/VisitorHit?user=Nattapat2871&repo=BsruRTP&countColor=%237B1E7A&style=flat-square)

</div>

<p align= "center">
        <a href="/README.md">English</a>   <b>ภาษาไทย</b>　




ปลั๊กอินสุ่มวาร์ป (Random Teleport) ที่ทรงพลังและยืดหยุ่นสำหรับเซิร์ฟเวอร์ Spigot/Paper มาพร้อมระบบ RTP 2 รูปแบบที่แตกต่างกัน: ระบบ GUI สำหรับผู้เล่นทั่วไป และระบบ ZoneRTP ที่เป็น Event Timer สำหรับการวาร์ปแบบกลุ่ม ถูกออกแบบมาให้ปรับแต่งได้ง่ายสำหรับเซิร์ฟเวอร์ Minecraft ยุคใหม่


## ✨ ฟีเจอร์เด่น (Features)

### RTP ทั่วไป (/rtp)
- **ระบบ GUI:** เมนูที่ใช้งานง่ายสำหรับผู้เล่นในการเลือกโลกที่จะวาร์ป
- **ระบบนับถอยหลัง:** ผู้เล่นต้องยืนนิ่งๆ รอสักครู่ก่อนการวาร์ป
- **ระบบคูลดาวน์:** ป้องกันการสแปมคำสั่ง RTP
- **ค้นหาจุดวาร์ปที่ปลอดภัย:** อัลกอริทึมขั้นสูงในการหาจุดวาร์ปที่ปลอดภัย รวมถึงการตรวจสอบเพื่อหลีกเลี่ยงเพดาน Nether และทะเลลาวา

### ZoneRTP
- **ระบบจับเวลาแบบต่อเนื่อง (Looping Timer):** สร้างโซน RTP ที่มีเวลานับถอยหลังทำงานวนลูปตลอดเวลา เพื่อสร้าง Event ให้กับเซิร์ฟเวอร์
- **วาร์ปแบบกลุ่ม:** ผู้เล่นทุกคนที่อยู่ในโซนเมื่อเวลาหมด จะถูกวาร์ปไปยังตำแหน่งสุ่มเดียวกัน
- **เงื่อนไขของโซน:** กำหนดให้โซนต้องการให้ผู้เล่นยืนบนบล็อกที่กำหนด และต้องอยู่ในความสูงที่จำกัดได้
- **แสดงผลแบบสาธารณะ:** ใช้ PlaceholderAPI เพื่อแสดงเวลานับถอยหลังของโซนได้ทุกที่ในเซิร์ฟเวอร์

### ฟีเจอร์ทั่วไป
- **ปรับแต่งได้สูง:** สามารถปรับแต่ง GUI, ข้อความ, เสียง, รัศมี, และการจับเวลาได้ทั้งหมด
- **คำสั่งแอดมินครบครัน:** ชุดคำสั่งที่แข็งแกร่งสำหรับสร้าง, ลบ, และจัดการฟีเจอร์ทั้งหมดของปลั๊กอิน
- **รองรับหลายโลก:** ทำงานร่วมกับโลกปกติและโลกที่จัดการโดย **Multiverse-Core** ได้อย่างสมบูรณ์
- **รองรับ PlaceholderAPI:** เชื่อมต่อการทำงานเพื่อแสดงผลข้อมูลแบบไดนามิก

---
## 🎮 เวอร์ชั่นที่รองรับ (Compatibility)

- **Minecraft Version:** `1.17` - `1.21+`
- **Server Software:** Spigot, Paper, และเวอร์ชันแยก (forks) อื่นๆ

---
## 🛠️ การติดตั้ง (Installation)

1.  ดาวน์โหลดไฟล์ `.jar` ล่าสุดจากหน้า [Modrinth](https://modrinth.com/plugin/bsrurtp) หรือ [Releases](https://github.com/Nattapat2871/BsruRTP/releases) ก็ได้
2.  นำไฟล์ `.jar` ที่ดาวน์โหลดไปใส่ในโฟลเดอร์ `plugins` ของเซิร์ฟเวอร์
3.  (ทางเลือก) ติดตั้ง [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.624/) เพื่อใช้งาน Placeholder
4.  รีสตาร์ทเซิร์ฟเวอร์ของคุณ
5.  ตั้งค่าไฟล์ `config.yml` และ `zones.yml` ที่ถูกสร้างขึ้นได้ตามต้องการ

---
## 📋 คำสั่งและสิทธิ์ (Commands & Permissions)

| คำสั่ง | คำอธิบาย | สิทธิ์ (Permission) | ค่าเริ่มต้น |
| :--- | :--- | :--- | :--- |
| `/rtp` | เปิดเมนูสุ่มวาร์ป | `bsrurtp.use` | `true` |
| `/bsrurtp` | แสดงข้อมูลปลั๊กอิน | `bsrurtp.admin` | `op` |
| `/bsrurtp help` | แสดงคำสั่งทั้งหมด | `bsrurtp.admin` | `op` |
| `/bsrurtp reload` | รีโหลดคอนฟิกทั้งหมด | `bsrurtp.reload` | `op` |
| `/bsrurtp status` | แสดงสถานะของปลั๊กอิน | `bsrurtp.status` | `op` |
| `/bsrurtp tpzone <zone>` | วาร์ปไปที่จุดกลางของโซน | `bsrurtp.tpzone` | `op` |
| `/zonertp create ...` | สร้างโซน RTP ใหม่ | `bsrurtp.admin.zone` | `op` |
| `/zonertp remove <zone>` | ลบโซน RTP | `bsrurtp.admin.zone` | `op` |

---
## 🔌 Placeholders (PlaceholderAPI)

- `%bsrurtp_zone_secs_<ชื่อโซน>%`
    - แสดงผลวินาทีที่เหลืออยู่ของโซน RTP ที่กำลังนับถอยหลังแบบวนลูป
    - **ตัวอย่าง:** `%bsrurtp_zone_secs_spawn-event%` จะแสดงเวลานับถอยหลังของโซนที่ชื่อ "spawn-event"
    - จะแสดงคำว่า "N/A" หากไม่มีโซนชื่อนั้นอยู่ หรือยังไม่เริ่มนับถอยหลัง

---
## ⚙️ การตั้งค่า (Configuration)

ปลั๊กอินใช้ไฟล์ตั้งค่าหลัก 2 ไฟล์:

### `config.yml`
ไฟล์นี้ควบคุมการทำงานของคำสั่ง `/rtp` แบบปกติ, ข้อความส่วนกลาง, และการทำงานของ ZoneRTP

```yaml
# ความสูงในการตรวจสอบผู้เล่นใน ZoneRTP (นับจาก Y ของจุดศูนย์กลางโซนขึ้นไป)
# เช่น ถ้าตั้งเป็น 4 และ Y ของโซนคือ 65, ระบบจะตรวจสอบผู้เล่นที่อยู่ระหว่าง Y 65 ถึง 69
zone-vertical-check-height: 4

# โลกที่อนุญาตให้ใช้ /rtp ได้
allowWorlds:
  - world
  - world_nether
  - world_the_end

# รัศมีการสุ่มวาร์ป (นับจากจุดเกิดของโลก)
radius:
  world: 2000
  world_nether: 2000
  world_the_end: 2000

# การตั้งค่าคูลดาวน์สำหรับคำสั่ง /rtp
cooldown:
  enabled: true
  seconds: 5
  message: "&cกรุณารออีก {cooldown} วินาทีก่อนสุ่มวาร์ปอีกครั้ง!"

# การตั้งค่า GUI ของ /rtp
gui:
  title: "&aเลือกโลกสุ่มวาร์ป"
  slots:
    world: 11
    world_nether: 13
    world_the_end: 15
  icons:
    world:
      material: GRASS_BLOCK
      name: "&aสุ่มวาร์ป Overworld"
      lore:
        - "&7ออนไลน์: %server_online%"
        - "&7คลิกเพื่อสุ่มจุดวาร์ป"
    world_nether:
      material: NETHERRACK
      name: "&cสุ่มวาร์ป Nether"
      lore:
        - "&7คลิกเพื่อสุ่มจุดวาร์ป"
    world_the_end:
      material: END_STONE
      name: "&dสุ่มวาร์ป The End"
      lore:
        - "&7คลิกเพื่อสุ่มจุดวาร์ป"

# เสียงประกอบทั้งหมด
sounds:
  gui_click: "ui.button.click"
  waiting: "entity.enderman.teleport"
  teleport: "entity.player.levelup"
  fail: "block.note_block.bass"
  cancel_on_move: "ENTITY_VILLAGER_NO"
  zone_countdown_tick: "BLOCK_NOTE_BLOCK_HAT"
  zone_teleport: "ENTITY_ENDERMAN_TELEPORT"

# ข้อความทั้งหมดในปลั๊กอิน
messages:
  no_permission: "&cคุณไม่มีสิทธิ์ใช้งานคำสั่งนี้"
  reload_success: "&aรีโหลด config สำเร็จ!"
  reload_failed: "&cรีโหลด config ล้มเหลว!"
  world_not_allowed: "&cโลกนี้ไม่เปิดให้สุ่มวาร์ป"
  already_in_rtp: "&cคุณกำลังรอวาร์ปอยู่แล้ว"
  world_not_found: "&cไม่พบโลกนี้"
  teleport_fail: "&cสุ่มจุดวาร์ปล้มเหลว กรุณาลองใหม่"
  teleport_success: "&aวาร์ปสำเร็จ! ไปยัง {location}"
  rtp_start: "&eสุ่มวาร์ปในอีก {seconds} วินาที ห้ามออกจากบล็อคที่ยืนอยู่!"
  countdown_actionbar: "&eสุ่มวาร์ปในอีก {seconds} วิ"
  rtp_cancel_move: "&cยกเลิกวาร์ปเพราะคุณออกจากบล็อคเดิม"
  rtp_cancel_world: "&cยกเลิกวาร์ปเพราะคุณเปลี่ยนโลก"
  # ข้อความสำหรับ ZoneRTP
  zone_countdown_title: "&a&lRTP ZONE"
  zone_countdown_subtitle: "&fวาร์ปในอีก &e{seconds} &fวินาที!"
  zone_teleport_success: "&bคุณถูกวาร์ปโดย RTP Zone!"
