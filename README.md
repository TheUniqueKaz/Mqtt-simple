Chào bạn, đây là một file README.md hoàn chỉnh cho dự án TheUniqueKaz/Mqtt-simple của bạn, dựa trên toàn bộ quá trình chúng ta đã xây dựng và gỡ lỗi.

Chỉ cần sao chép, dán nội dung dưới đây vào file README.md trong thư mục gốc của dự án là xong.

(Nội dung file README.md bắt đầu từ đây)

Mqtt-simple
Một dự án Spring Boot đơn giản để demo cách kết nối, lắng nghe (subscribe) và xử lý tin nhắn từ một MQTT Broker.

Dự án này sử dụng Spring Integration để tạo một luồng (flow) xử lý tin nhắn, tự động kết nối và đăng ký vào một topic trên Broker khi ứng dụng khởi động. Nó cũng được tích hợp sẵn Spring Data JPA và trình điều khiển PostgreSQL để sẵn sàng cho việc lưu trữ dữ liệu vào database.

🚀 Tính năng chính
Tự động kết nối: Tự động kết nối đến MQTT Broker khi khởi động.

Tự động đăng ký: Tự động đăng ký (subscribe) vào một topic được định nghĩa trong application.properties.

Xử lý tin nhắn: In ra log (console) nội dung các tin nhắn nhận được.

Tích hợp Database: Cấu hình sẵn để kết nối với CSDL PostgreSQL.

Java DSL: Sử dụng Spring Integration Java DSL (IntegrationFlow) để định nghĩa luồng xử lý tin nhắn một cách rõ ràng.

🛠️ Công nghệ sử dụng
Java 21

Spring Boot 3.3+

Spring Integration (Adapter spring-integration-mqtt v3)

Spring Data JPA

PostgreSQL (Driver)

Maven
