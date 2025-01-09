Lỗi **`'iot_ws_simple_4' already exists in the index`** xảy ra vì thư mục `iot_ws_simple_4` đã tồn tại trong repository và được Git theo dõi. Để thêm lại submodule vào thư mục này, bạn cần thực hiện một số bước để xóa thông tin cũ trước khi thêm lại submodule.

---

### Các bước thêm lại submodule

#### 1. **Xóa thông tin submodule cũ**
- Chạy lệnh sau để xóa tham chiếu đến `iot_ws_simple_4`:
  ```bash
  git rm --cached iot_ws_simple_4
  ```

- Xóa thư mục con trong `.git/modules/` nếu nó tồn tại:
  ```bash
  rm -rf .git/modules/iot_ws_simple_4
  ```

- Xóa thư mục `iot_ws_simple_4` trong repository:
  ```bash
  rm -rf iot_ws_simple_4
  ```

#### 2. **Commit thay đổi**
- Commit việc xóa thông tin cũ:
  ```bash
  git commit -m "Remove old submodule iot_ws_simple_4"
  ```

---

#### 3. **Thêm lại submodule**
- Thêm lại submodule với URL của repository:
  ```bash
  git submodule add https://github.com/HAI4831/iot_ws_simple.git iot_ws_simple_4
  ```

#### 4. **Cập nhật submodule**
- Cập nhật và tải nội dung submodule:
  ```bash
  git submodule update --init --recursive
  ```

#### 5. **Commit thông tin submodule**
- Commit thay đổi liên quan đến submodule:
  ```bash
  git commit -m "Add submodule iot_ws_simple_4"
  ```

---

### Kiểm tra sau khi thêm lại submodule
- Kiểm tra trạng thái submodule:
  ```bash
  git submodule
  ```

- Kiểm tra file `.gitmodules` để đảm bảo submodule đã được thêm thành công:
  ```bash
  cat .gitmodules
  ```

---

Sau khi hoàn thành các bước trên, submodule `iot_ws_simple_4` sẽ được thêm lại vào repository của bạn. Nếu còn lỗi nào khác, hãy cho tôi biết! 😊