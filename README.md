## Project: học lập trình game.

- https://www.youtube.com/watch?v=om59cwR7psI&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq
	
## Tính năng

## Cập nhật
	
## Cách chạy

1. Mở project trong Eclipse.
2. Chạy class `Main.java` bằng cách click chuột phải → Run As → Java Application.

## Yêu cầu hệ thống

- Java SE 17 trở lên.
	
## [1.0.1] - 2025-08-06

### Lỗi 

- Vấn đề trong code
	
	Mảng fixed size
	
	private final SuperObject[] obj = new SuperObject[10];
	private final Entity[] npc = new Entity[10];
	
	→ Hạn chế số lượng object và NPC (ví dụ nếu cần 50 NPC thì phải đổi tất cả code liên quan).
	
	Null check nhiều lần
	Ở update() và khi build drawList, bạn phải check if (npc != null) lặp đi lặp lại.
	
	Khó mở rộng
	Object và NPC đang lưu ở 2 mảng riêng biệt → lúc render phải merge thủ công vào drawList.

### Sửa lỗi

### PS

- Kết quả

	Dễ mở rộng số lượng NPC/Objects (chỉ cần list.add()).
	
	Bớt code rườm rà (null check, index thủ công).
	
	Dễ tổ chức hơn nếu sau này chia map thành nhiều loại entity khác nhau.
	
### Comment

- Thiết lập khung hình và đối tượng
- Thêm xử lý khóa
- Thêm delta/bộ tích lũy Vòng lặp trò chơi
- Thêm FPS
- Thêm nhấn chuột phải di chuyển nhân vật
- thêm hình ảnh nhân vật
- Add player animation
- Add tiles
- Add map loader
- Add camera following character
- Add collision detection

### So sánh

### Ý tưởng

- tối ưu(đang làm)

- gom logic kẹp biên + tính screenX/screenY vào một CameraHelper để player, object, NPC đều gọi chung. Nếu sau này bạn đổi cách camera hoạt động (ví dụ thêm zoom, rung màn hình), chỉ cần sửa trong CameraHelper.(đã làm)

- Mouse (Chưa sửa)

- Va chạm npc với npc thêm (target != entity) để tránh npc va chạm với chính nó (đã làm)

- Va chạm npc với player (đã làm)

- va chạm player với npc (đã làm)

- 



