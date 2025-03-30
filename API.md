## 📘 API 명세서

### 🔐 Auth API

| 기능     | 메서드 | URL             | 요청 헤더 | 요청 바디 | 응답 바디 | 상태 코드 |
|----------|--------|------------------|-----------|-----------|------------|------------|
| 회원가입 | POST   | `/auth/signup`   | -         | `{ "email": "...", "password": "...", "nickname": "...", "birthday": "...", "phoneNumber": "..." }` | `"JWT TOKEN"` | 201, 400 |
| 로그인   | POST   | `/auth/signin`   | -         | `{ "email": "...", "password": "..." }` | `"JWT TOKEN"` | 200, 400, 404 |

---

### 🎤 공연 API

| 기능           | 메서드 | URL                    | 요청 헤더 | 요청 바디 | 응답 바디 | 상태 코드 |
|----------------|--------|-------------------------|-----------|-----------|------------|------------|
| 공연 생성       | POST   | `/shows`                | Authorization: Bearer {token} | `{ title, category, content, region, startDate, endDate, reservationStartDate, reservationEndDate, imageUrl, seats[] }` | - | 201, 400, 401 |
| 공연 목록 조회   | GET    | `/shows`                | Authorization: Bearer {token} | - | 공연 리스트 | 200, 400, 401, 404 |
| 특정 공연 조회   | GET    | `/shows/{showId}`       | Authorization: Bearer {token} | - | 공연 상세 | 200, 400, 401, 404 |
| 공연 수정       | PUT    | `/shows/{showId}`       | Authorization: Bearer {token} | `{ title, category, ... }` | - | 200, 400, 401, 404 |
| 공연 삭제       | DELETE | `/shows/{showId}`       | Authorization: Bearer {token} | - | - | 200, 401, 404 |

---

### 🍇 좌석 API

| 기능         | 메서드 | URL                                | 요청 헤더 | 요청 바디 | 응답 바디 | 상태 코드 |
|--------------|--------|-------------------------------------|-----------|-----------|------------|------------|
| 좌석 수정     | PUT    | `/shows/{showId}/seats/{seatId}`   | Authorization: Bearer {token} | `{ name, count, price }` | - | 200, 400, 401, 404 |
| 좌석 조회     | GET    | `/shows/{showId}/seats`            | Authorization: Bearer {token} | - | 좌석 리스트 | 200, 401, 404 |

---

### 🎟 예매 API

| 기능       | 메서드 | URL                     | 요청 헤더 | 요청 바디 | 응답 바디 | 상태 코드 |
|------------|--------|--------------------------|-----------|-----------|------------|------------|
| 예매 요청   | POST   | `/reservations`          | Authorization: Bearer {token} | `{ userId, showId, seatId }` | `{ reservationId, price, seatInfo }` | 201, 400, 401, 404, 429 |
| 예매 조회   | GET    | `/reservations/{id}`     | Authorization: Bearer {token} | - | 예매 상세 | 200, 401, 404 |
| 예매 취소   | DELETE | `/reservations/{id}`     | Authorization: Bearer {token} | - | - | 200, 400, 401, 404 |