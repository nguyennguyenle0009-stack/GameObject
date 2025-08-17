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

### Sửa lỗi

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

- Chỉnh va chạm tại vị trí góc của cây tính từ trên cùng bên trái màn hình và trên cùng bên trái nơi bắt đầu vẽ cây(#B đã làm)
	BUG
	------------------------------------------------
	⚠️ Vấn đề: collisionArea đang bị cộng sai tọa độ
	object.getCollisionArea().y = object.getWorldY() + object.getCollisionArea().y; Nhưng object.getCollisionArea().y đã có giá trị offset, nên khi cộng thêm object.getWorldY() vào chính nó, là đang cộng chồng lên offset cũ
	
	Giải quyết
	------------------------------------------------
	Dùng biến tạm để tính vùng va chạm thực tế, không sửa trực tiếp collisionArea
	Rectangle entityArea = new Rectangle(
	    entity.getWorldX() + entity.getCollisionArea().x,
	    entity.getWorldY() + entity.getCollisionArea().y,
	    entity.getCollisionArea().width,
	    entity.getCollisionArea().height
	);
	
	Rectangle objectArea = new Rectangle(
	    object.getWorldX() + object.getCollisionArea().x,
	    object.getWorldY() + object.getCollisionArea().y,
	    object.getCollisionArea().width,
	    object.getCollisionArea().height
	);
	------------------------------------------------
	=> Sửa hàm CheckObject
- Chỉnh tầm nhìn khi nhân vật và object thay đổi vị trí theo góc nhìn trục y từ dưới lên(#B đã làm)
	+ thiết lập phần va chạm ủa nhân vật và object ở gốc tính tương đương 3d
	+ tính chuyển động nhân vật và nếu nhân vật trước object thì nhân vật che object (characterFootPosition)
	+ vị trí object luôn đứng im nên không cần tính vị trí sau chuyển động
- nâng cao(Chưa làm)

	 ý tưởng xây dựng map phức tạp (nhà, cây, cột, tường…):
	- Dùng tiled editor như Tiled Map Editor để vẽ map rồi import vào Java.
	- Hoặc tạo một hệ thống ObjectLayer riêng, trong đó mỗi object có:
	- anchorY để xác định gốc.
	- drawOffsetY để vẽ hình ảnh lệch lên.
	- collisionArea nằm ở chân.
- tối ưu(chưa làm)






