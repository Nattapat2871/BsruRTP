# 🔌 ปลั๊กอิน bsruRTP

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

ปลั๊กอินสุ่มวาร์ป (Random Teleport) ที่เรียบง่ายแต่ทรงพลังสำหรับเซิร์ฟเวอร์ Spigot/Paper มาพร้อมกับเมนู (GUI) ที่ใช้งานง่ายสำหรับผู้เล่นในการสุ่มวาร์ปไปยังโลกต่างๆ ที่กำหนดไว้ ปลั๊กอินสามารถปรับแต่งได้อย่างละเอียด ทำให้เจ้าของเซิร์ฟเวอร์สามารถปรับเปลี่ยนประสบการณ์การวาร์ปได้เกือบทุกแง่มุม

---
## ✨ ฟีเจอร์เด่น (Features)

- **ระบบ GUI:** ใช้งานง่ายผ่านเมนู ไม่ต้องพิมพ์คำสั่งที่ยุ่งยาก
- **ปรับแต่งได้เต็มรูปแบบ:** สามารถปรับแต่งหน้าตา GUI, ข้อความ, เสียง, และไอเทมต่างๆ ได้ทั้งหมดใน `config.yml`
- **รัศมีต่อโลก:** กำหนดรัศมีการสุ่มวาร์ปที่แตกต่างกันสำหรับแต่ละโลกได้
- **ระบบคูลดาวน์:** ป้องกันผู้เล่นสแปมคำสั่งสุ่มวาร์ป
- **ระบบหน่วงเวลาก่อนวาร์ป:** ผู้เล่นต้องยืนนิ่งๆ รอสักครู่ก่อนที่จะถูกวาร์ป
- **รองรับ Multiverse-Core:** ทำงานร่วมกับโลกที่จัดการโดย Multiverse-Core ได้อย่างสมบูรณ์
- **รองรับ PlaceholderAPI:** สามารถใช้ Placeholder ในส่วนของ Lore ของไอเทมใน GUI ได้ (เป็นแค่ตัวเลือกเสริม)
- **คำสั่ง Reload:** รีโหลดคอนฟิกได้ทันทีด้วยคำสั่ง `/bsrurtp reload`

---
## 🎮 เวอร์ชั่นที่รองรับ (Compatibility)

- **Minecraft Version:** `1.19` - `1.21.7`
- **Server Software:** Spigot, Paper และเวอร์ชันแยก (forks) อื่นๆ

---
## 🛠️ การติดตั้ง (Installation)

1.  ดาวน์โหลดไฟล์ `.jar` ล่าสุดจากหน้า [Releases](https://github.com/Nattapat2871/BsruRTP/releases)
2.  นำไฟล์ `.jar` ที่ดาวน์โหลดไปใส่ในโฟลเดอร์ `plugins` ของเซิร์ฟเวอร์
3.  รีสตาร์ทเซิร์ฟเวอร์ของคุณ
4.  ปลั๊กอินจะสร้างโฟลเดอร์ `bsruRTP` พร้อมกับไฟล์ `config.yml` ให้คุณเข้าไปปรับแต่งได้ตามต้องการ

---
## 🔗 ปลั๊กอินที่ทำงานร่วมกัน (Dependencies)

- **PlaceholderAPI** (ตัวเลือกเสริม): หากติดตั้งปลั๊กอินนี้ จะสามารถใช้ Placeholder ต่างๆ ในส่วนของ `lore` ของไอเทมใน GUI ได้ (ปลั๊กอินยังทำงานได้ปกติแม้ไม่มี PAPI)

---
## 📋 คำสั่งและสิทธิ์ (Commands & Permissions)

| คำสั่ง | คำอธิบาย | สิทธิ์ (Permission) |
| :--- | :--- | :--- |
| `/rtp` | เปิดเมนูสุ่มวาร์ป | `bsrurtp.use` |
| `/bsrurtp reload` | รีโหลดไฟล์คอนฟิกของปลั๊กอิน | `bsrurtp.reload` (สำหรับแอดมิน) |

---
## ⚙️ การตั้งค่า (`config.yml`)

ด้านล่างคือตัวอย่างไฟล์คอนฟิกพร้อมคำอธิบายของแต่ละส่วน

```yaml
# ตั้งค่า GUI
gui:
  title: "&aเลือกโลกสุ่มวาร์ป"
  # กำหนด slot ของแต่ละโลก (0-26)
  slots:
    world: 11
    world_nether: 13
    world_the_end: 15
  # กำหนดไอคอนของแต่ละโลก
  icons:
    world:
      material: "GRASS_BLOCK"
      name: "&a&lสุ่มวาร์ป &f&lOverworld"
      lore:
        - "&7คลิกเพื่อสุ่มวาร์ปในโลกธรรมดา"
        - "&7รัศมี: 2000 บล็อก"
        - "&fผู้เล่นออนไลน์: &a%server_online%"
    world_nether:
      material: "NETHERRACK"
      name: "&c&lสุ่มวาร์ป &f&lNether"
      lore:
        - "&7คลิกเพื่อสุ่มวาร์ปในโลกเนเธอร์"
        - "&7รัศมี: 1000 บล็อก"
    world_the_end:
      material: "END_STONE"
      name: "&5&lสุ่มวาร์ป &f&lThe End"
      lore:
        - "&7คลิกเพื่อสุ่มวาร์ปในโลกดิเอนด์"
        - "&7รัศมี: 5000 บล็อก"

# รายชื่อโลกที่อนุญาตให้ rtp ได้ (ต้องตรงกับชื่อโฟลเดอร์โลก)
allowWorlds:
  - "world"
  - "world_nether"
  - "world_the_end"

# รัศมีการสุ่ม (นับจากจุดเกิดของโลกนั้นๆ)
radius:
  world: 2000
  world_nether: 1000
  world_the_end: 5000

# ตั้งค่าคูลดาวน์
cooldown:
  enabled: true
  seconds: 60
  message: "&cกรุณารออีก {cooldown} วินาทีก่อนสุ่มวาร์ปอีกครั้ง!"

# ข้อความต่างๆ ในปลั๊กอิน
messages:
  reload_success: "&aรีโหลด config สำเร็จ!"
  world_not_allowed: "&cไม่สามารถสุ่มวาร์ปในโลกนี้ได้"
  already_in_rtp: "&cคุณกำลังรอวาร์ปอยู่แล้ว"
  rtp_start: "&eสุ่มวาร์ปในอีก 5 วินาที ห้ามออกจากบล็อคที่ยืนอยู่!"
  countdown_actionbar: "&eสุ่มวาร์ปในอีก {seconds} วิ"
  rtp_cancel_move: "&cยกเลิกวาร์ปเพราะคุณออกจากบล็อคเดิม"
  rtp_cancel_world: "&cยกเลิกวาร์ปเพราะคุณเปลี่ยนโลก"
  # ข้อความสำหรับ ZoneRTP
  zone_countdown_title: "&a&lRTP ZONE"
  zone_countdown_subtitle: "&fวาร์ปในอีก &e{seconds} &fวินาที!"
  zone_teleport_success: "&bคุณถูกวาร์ปโดย RTP Zone!"