# Facebook Clone

Một nền tảng mạng xã hội hiện đại, hỗ trợ các tính năng cơ bản đến nâng cao, xây dựng bằng **Spring Boot + Angular** với **Realtime WebSocket**.

---
## Công nghệ
- **Java**: 21
  
- **Spring Boot**: 3.5.5
  
- **MySQL**: 8.0.42
  
- **Angular CLI**: 17.0.6
  
- **Node.js**: 20.19.3
  
- **npm**: 11.2.0

- **Redis**: 8.0-M03-alpine

## Tính năng

### Authentication
- ✔ Đăng ký / Đăng nhập
- ✔ Refresh token
- ✔ Quên mật khẩu

### Hồ sơ cá nhân
- ✔ Avatar, cover, bio, ảnh, bạn bè
- ✔ Chỉnh sửa thông tin hồ sơ

### Bài viết (Post)
- ✔ Text + ảnh/video
- ✔ Quyền riêng tư (Public / Friends / Only me)
- ✔ Edit / Delete
- ✔ Pagination

### Tương tác
- ✔ Like
- ✔ Comment (phân cấp)
- ✔ Like Comment
- ✔ Mention @user

### Kết nối
- ✔ Gửi lời mời kết bạn, hủy, chấp nhận
- ✔ Gợi ý bạn bè

### News Feed
- ✔ Feed cá nhân + Global (bạn bè + bản thân)
- ✔ Infinite Scroll

### Thông báo (Realtime Notification)
- ✔ Sự kiện: Like, Comment, Yêu cầu kết bạn, Tin nhắn
- ✔ Mark as read + Xóa thông báo
- ✔ Batch notification (gom nhiều like/comment gần nhau)
- ✔ Realtime performance (có thể mở rộng Kafka/Redis pubsub)

### Nhắn tin
- ✔ Chat 1-1 (conversation)
- ✔ Realtime online status
- ✔ Hiển thị trạng thái “đang nhập…”

---

## Tính năng dự kiến (chưa làm)

### Tìm kiếm
- ✘ Tìm kiếm người dùng, bài viết (full-text)
- ✘ Nâng cấp Elasticsearch

### Báo cáo / Ẩn / Block
- ✘ Report bài viết hoặc người dùng
- ✘ Block user

### Quản trị cơ bản
- ✘ Khóa user
- ✘ Xóa nội dung vi phạm

### Group / Community
- ✘ Người dùng tạo nhóm, post trong nhóm
- ✘ Admin group quản lý post/members

### Story (24h)
- ✘ Đăng hình/video chỉ hiển thị trong 24h

### Notification nâng cao
- ✘ Web Push / Mobile Push (Firebase)

### Search mạnh hơn
- ✘ Elasticsearch gợi ý real-time + highlight

### Realtime Performance nâng cao
- ✘ Kafka hoặc Redis pub/sub cho feed + noti + chat

### Gợi ý thông minh
- ✘ ML gợi ý bạn bè (dựa trên bạn chung, sở thích, tương tác)

### Security
- ✘ 2FA login
- ✘ Revoke session từ Admin/User

### Moderation
- ✘ AI filter ngôn ngữ xấu, hình ảnh nhạy cảm

### Gamification
- ✘ Badge, Level, Điểm dựa trên hoạt động

---




