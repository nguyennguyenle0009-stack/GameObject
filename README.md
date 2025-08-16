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

- Cây di chuyển khi nhân vật di chuyển

### Sửa lỗi

- Khi camera còn cuộn được: object dùng công thức thường (đi cùng map).
- Khi camera bị ghim ở mép: object chuyển sang hệ quy chiếu “world → screen” giống như tile & player đang dùng, nên không còn trôi lệch nữa.
- Điều kiện hiển thị giữ nguyên: chỉ vẽ khi nằm trong khung hình.

### PS

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

- cố định FPS (fixed timestep) → đảm bảo game chạy mượt, không phụ thuộc vào tốc độ CPU.

