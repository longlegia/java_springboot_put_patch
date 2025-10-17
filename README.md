# SO SÁNH PUT vs PATCH TRONG SPRING BOOT REST API

## Mục tiêu

* Minh họa sự khác biệt giữa phương thức PUT (cập nhật toàn bộ) và PATCH (cập nhật một phần) trong API CRUD.

* Dữ liệu ban đầu (sau khi POST)

```
POST http://localhost:8080/api/users
Content-Type: application/json

{
  ""name"": ""Anh Hào"",
  ""email"": ""anhhao@gmail.com""
}
```

* Response

```
{
  ""id"": 3,
  ""name"": ""Anh Hào"",
  ""email"": ""anhhao@gmail.com""
}
```

User với id = 3 được tạo thành công.

Thử nghiệm 1: Dùng PUT (Cập nhật TOÀN BỘ)
```
PUT http://localhost:8080/api/users/3
Content-Type: application/json

{
  ""name"": ""Anh Hào""
}
```
* Response:
```
{
  ""id"": 3,
  ""name"": ""Anh Hào"",
  ""email"": null  # BỊ MẤT EMAIL!
}
```

* Nhận xét:
  * PUT thay thế toàn bộ tài nguyên.
  * Vì request không gửi email, hệ thống coi như email = null.
  * Dễ gây mất dữ liệu nếu client quên gửi đủ field.

* Thử nghiệm 2: Dùng PATCH (Cập nhật MỘT PHẦN)

* Request:

```
PATCH http://localhost:8080/api/users/3
Content-Type: application/json

{
  ""email"": ""anhhao@gmail.com""
}
```
* Response: 
```
{
  ""id"": 3,
  ""name"": ""Anh Hào"",          #  GIỮ NGUYÊN
  ""email"": ""anhhao@gmail.com"" # CHỈ CẬP NHẬT EMAIL
}
```

* Nhận xét:
 * PATCH chỉ cập nhật những trường được gửi.
 * Các trường khác (name) giữ nguyên giá trị cũ.
 * An toàn hơn khi chỉ muốn sửa một vài thông tin.


Code controller minh họa đơn giản:

```
@RestController
@RequestMapping(""/api/users"")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PutMapping(""/{id}"")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id)
            .map(user -> {
                user.setName(userDetails.getName());
                user.setEmail(userDetails.getEmail()); // Nếu null → gán null!
                return ResponseEntity.ok(userRepository.save(user));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping(""/{id}"")
    public ResponseEntity<User> partialUpdateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return userRepository.findById(id)
            .map(user -> {
                if (updates.containsKey(""name"")) {
                    user.setName((String) updates.get(""name""));
                }
                if (updates.containsKey(""email"")) {
                    user.setEmail((String) updates.get(""email""));
                }
                return ResponseEntity.ok(userRepository.save(user));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
```
