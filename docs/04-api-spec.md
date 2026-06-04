# API Spec Template

## 1. API Overview

Base URL:

```text

```

Local development example:

```text
http://localhost:3000/api
```

Authentication method:

```text
Authorization: Bearer <token>
```

## 2. Common Response Format

### Success Response

```json
{
  "success": true,
  "data": {}
}
```

### Error Response

```json
{
  "success": false,
  "message": "Error message"
}
```

## 3. Common Error Codes

| Status | Meaning | Example |
|---:|---|---|
| 400 | Bad Request | Invalid input |
| 401 | Unauthorized | Login required |
| 403 | Forbidden | No permission |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Duplicate request |
| 500 | Server Error | Unexpected error |

## 4. Authentication APIs

### POST /auth/register

Description:

```text

```

Auth required:

```text
No
```

Request body:

```json
{
  "email": "",
  "password": "",
  "name": ""
}
```

Response:

```json
{
  "success": true,
  "data": {
    "user": {},
    "token": ""
  }
}
```

Error cases:

| Status | Reason |
|---:|---|
| 400 | Invalid input |
| 409 | Email already exists |

---

### POST /auth/login

Description:

```text

```

Auth required:

```text
No
```

Request body:

```json
{
  "email": "",
  "password": ""
}
```

Response:

```json
{
  "success": true,
  "data": {
    "user": {},
    "token": ""
  }
}
```

Error cases:

| Status | Reason |
|---:|---|
| 400 | Invalid input |
| 401 | Wrong email or password |

---

### GET /auth/me

Description:

```text

```

Auth required:

```text
Yes
```

Response:

```json
{
  "success": true,
  "data": {
    "user": {}
  }
}
```

Error cases:

| Status | Reason |
|---:|---|
| 401 | Missing or invalid token |

---

### POST /auth/logout

Description:

```text

```

Auth required:

```text
Yes
```

Response:

```json
{
  "success": true,
  "message": "Logged out"
}
```

## 5. Feature API Template

Copy this section for each feature.

---

### GET /resource

Description:

```text

```

Auth required:

```text

```

Role required:

```text

```

Query parameters:

| Name | Type | Required | Description |
|---|---|---:|---|
| page | number | No | Page number |
| limit | number | No | Items per page |
| search | string | No | Search keyword |

Response:

```json
{
  "success": true,
  "data": []
}
```

Error cases:

| Status | Reason |
|---:|---|
| 401 | Login required |
| 403 | No permission |
| 500 | Server error |

---

### GET /resource/:id

Description:

```text

```

Auth required:

```text

```

Role required:

```text

```

Path parameters:

| Name | Type | Required | Description |
|---|---|---:|---|
| id | string | Yes | Resource ID |

Response:

```json
{
  "success": true,
  "data": {}
}
```

Error cases:

| Status | Reason |
|---:|---|
| 401 | Login required |
| 403 | No permission |
| 404 | Resource not found |

---

### POST /resource

Description:

```text

```

Auth required:

```text

```

Role required:

```text

```

Request body:

```json
{
  "title": "",
  "description": ""
}
```

Response:

```json
{
  "success": true,
  "data": {}
}
```

Error cases:

| Status | Reason |
|---:|---|
| 400 | Invalid input |
| 401 | Login required |
| 403 | No permission |

---

### PATCH /resource/:id

Description:

```text

```

Auth required:

```text

```

Role required:

```text

```

Request body:

```json
{
  "title": "",
  "description": ""
}
```

Response:

```json
{
  "success": true,
  "data": {}
}
```

Error cases:

| Status | Reason |
|---:|---|
| 400 | Invalid input |
| 401 | Login required |
| 403 | No permission |
| 404 | Resource not found |

---

### DELETE /resource/:id

Description:

```text

```

Auth required:

```text

```

Role required:

```text

```

Response:

```json
{
  "success": true,
  "message": "Deleted successfully"
}
```

Error cases:

| Status | Reason |
|---:|---|
| 401 | Login required |
| 403 | No permission |
| 404 | Resource not found |

## 6. API Checklist

- [ ] All protected APIs require authentication.
- [ ] Admin APIs check admin role.
- [ ] Request body is validated.
- [ ] Error responses are consistent.
- [ ] Sensitive data is not returned.
- [ ] API names are RESTful and predictable.
- [ ] Frontend API client paths match backend routes.