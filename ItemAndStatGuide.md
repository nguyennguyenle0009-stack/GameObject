# Hướng dẫn thêm trang bị và chỉ số mới

## Thêm trang bị mới
1. Thêm dòng vào bảng `Items` với `ItemId`, `Name` và `Type` (ví dụ `WEAPON`, `ARMOR`, `HEALTH_POTION`...).
2. Nếu là trang bị, khai báo các chỉ số cơ bản tại bảng `ItemStatMods` bằng cách tạo bản ghi mới cho từng thuộc tính (`Stat`, `Flat`).
3. Nếu muốn trang bị rơi ngẫu nhiên, sử dụng `ItemGenerator.createFromTemplate` với `ItemId` của mẫu vừa tạo.

## Thêm chỉ số mới
1. Thêm hằng mới vào enum `game.enums.Attr`.
2. Cập nhật bảng `PlayerBaseStats` và các bảng liên quan trong `sql/game_db_core_schema.sql` nếu cần lưu vào cơ sở dữ liệu.
3. Các lớp `Attributes`, `Player.refreshStats()` và những nơi tính toán khác có thể cần điều chỉnh để sử dụng thuộc tính mới.

Xem `README.md` để biết thêm thông tin chi tiết.
