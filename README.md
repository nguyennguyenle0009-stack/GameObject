## Project: học lập trình game.

- https://www.youtube.com/watch?v=om59cwR7psI&list=PL_QPQmz5C6WUF-pOQDsbsKbaBZqXj4qSq
	
## Tính năng

## Cập nhật

## [1.0.8] - 2025-08-27

### Thêm
- Bảng **Công pháp** di chuyển sang bên phải, có nút *Use* và *Gán* cho từng kỹ năng.
- Hiển thị thời gian hồi chiêu bên cạnh tên công pháp và trong HUD.
- Tooltip và con lăn chuột hỗ trợ cuộn trong danh sách công pháp.
- Kho đồ và bảng công pháp đều hỗ trợ cuộn bằng chuột.

### Sửa lỗi
- Sử dụng sách công pháp giờ được lưu vào tệp, nhân vật giữ nguyên công pháp sau khi thoát.

### Cải thiện
- Tệp lưu bổ sung thông tin công pháp với cấp độ và phẩm cấp.

## [1.0.7] - 2025-08-26

### Thêm
- Hệ thống **Kỹ năng** cơ bản với class `Skill` dễ mở rộng.
- 4 công pháp tu luyện (hạ, trung, thượng, cực phẩm) học qua sách.
- 4 loại đan dược tăng tốc tu luyện, cộng thêm SPIRIT mỗi giây trong 10 phút.
- Tu luyện đứng im, có nút **Huỷ** ở HUD và hồi chiêu 1 giờ.
- HUD hiển thị tên đan dược đang dùng và thời gian đếm ngược.
- Bảng thuộc tính có nút mở danh sách công pháp.
- Tệp lưu người chơi thêm mục `SKILL` ghi lại công pháp đã học.

### Sửa lỗi
- Sử dụng sách công pháp/đan dược giảm đúng số lượng và được ghi vào tệp lưu.
- HUD hiện thời gian hồi chiêu tu luyện.

### Cải thiện
- Ô chứa item nhỏ 32px, kho có thêm thanh cuộn để tránh tràn.
- Khung thuộc tính thu gọn, chữ cỡ nhỏ và căn lề sát hơn.

## [1.0.6] - 2025-08-25

### Sửa lỗi
- Gỡ giới hạn Attack/Def để chỉ số tăng đúng khi thăng cấp.
- Thể chất **Ngũ Hành** yêu cầu SPIRIT gấp 3 lần so với thể chất khác.

## [1.0.5] - 2025-08-25

### Thay đổi
- Điều chỉnh hệ thống tăng cấp theo **Cảnh giới**:
  - Luyện thể và Luyện khí tăng chỉ số theo công thức mới.
  - Đột phá đại cảnh giới nhân đôi (hoặc gấp ba với DEF) chỉ số và cộng thêm Linh hồn.
  lỗi: attack và def không cộng dồn khi thể chất bình thường

### Thêm
- Hệ thống **Thể chất** và **Linh căn** khi khởi tạo nhân vật.
- Mở rộng hệ thống **Cảnh giới** với Luyện thể/Luyện khí và tăng chỉ số khi lên cấp.
- Item mới **Đan dược tinh thần** giúp cộng Spirit để kiểm tra lên cấp.
- HUD và bảng thuộc tính hiển thị tên cảnh giới, thể chất và linh căn hiện tại.

### Sửa lỗi
- Sử dụng item không còn đặt lại máu về 100 mà giới hạn theo chỉ số tối đa.

## [1.0.3] - 2025-08-08

### Thêm
- Hệ thống **Cảnh giới** cho nhân vật, hiển thị tại HUD.

### Sửa lỗi
- HUD bị phóng to khi hiển thị tên NPC.



### Sửa lỗi
- Ẩn mô tả item khi chuột rời ô.


### Bug phát hiện
- Tooltip của item tiếp tục hiển thị khi rời ô (đã sửa).
	
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

