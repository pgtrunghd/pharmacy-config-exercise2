# Bài 2 — Cập nhật VAT không khởi động lại Pharmacy Service

Đề bài: https://portal.rikkei.edu.vn/learn/159/homework/8289

Phát triển tiếp bài 1: Config Server đọc `pharmacy-service.properties` từ Git; Pharmacy Service kết nối PostgreSQL, có `BillController` với `@RefreshScope`. VAT ban đầu là `8` (%). Giá trị VAT và cấu hình PostgreSQL nằm trong Git.

## Chạy

Cần JDK 21, Git và PostgreSQL có database `pharmacy_db`. Trên macOS nếu máy đang mặc định dùng JDK khác: `export JAVA_HOME=$(/usr/libexec/java_home -v 21)`. Chạy các lệnh từ thư mục gốc repository, sau khi đã commit file cấu hình trên nhánh `main`.

Terminal 1:

```bash
./gradlew :config-server:bootRun
```

Terminal 2:

```bash
DB_PASSWORD='mat-khau-cua-ban' ./gradlew :pharmacy-service:bootRun
```

Có thể đặt thêm `DB_PORT`, `DB_NAME`, `DB_USERNAME` nếu khác giá trị mặc định. Config Server chạy ở `8888`, Pharmacy Service ở `8081`. Config Server mặc định đọc repository hiện tại. Có thể đổi bằng `CONFIG_REPO_URI`.

## Tính tiền và cập nhật VAT

```bash
curl -fsS -X POST http://localhost:8081/api/v1/bill -H 'Content-Type: application/json' -d '{"medicineTotal":100000}'
```

Với VAT 8%, kết quả là `medicineTotal=100000.00`, `vatAmount=8000.00`, `total=108000.00`.

Sửa `pharmacy.vat-rate=10` trong `pharmacy-service.properties`, rồi commit (và push nếu Config Server đang đọc GitHub). Không khởi động lại ứng dụng.

```bash
curl -fsS -X POST http://localhost:8081/actuator/refresh
curl -fsS -X POST http://localhost:8081/api/v1/bill -H 'Content-Type: application/json' -d '{"medicineTotal":100000}'
```

Sau refresh, tổng tiền là `110000.00`. API từ chối tổng tiền âm hoặc thiếu. Chỉ endpoint `health` và `refresh` của Actuator được mở qua HTTP. Endpoint `refresh` nên được giới hạn truy cập khi triển khai ngoài môi trường thực hành.

## Build

```bash
./gradlew clean test bootJar
```
