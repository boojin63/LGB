# Requirements Template

## 1. User Roles

Define all user roles.

| Role | Description | Main Permissions |
|---|---|---|
| Guest | Not logged in user | |
| User | Normal logged in user | |
| Admin | Service manager | |

Additional roles if needed:

| Role | Description | Main Permissions |
|---|---|---|
|  |  |  |
|  |  |  |

## 2. Functional Requirements

List what the system must do.

### 2.1 Authentication

- [ ] Sign up
- [ ] Login
- [ ] Logout
- [ ] Restore login session
- [ ] Update profile
- [ ] Password reset
- [ ] Role-based access control

Notes:

```text

```

### 2.2 User Features

- [ ] View main page
- [ ] View list
- [ ] View detail
- [ ] Create data
- [ ] Update own data
- [ ] Delete own data
- [ ] Search
- [ ] Filter
- [ ] Sort

Notes:

```text

```

### 2.3 Admin Features

- [ ] View admin dashboard
- [ ] View all users
- [ ] View all data
- [ ] Approve/reject requests
- [ ] Edit any data
- [ ] Delete any data
- [ ] Manage service settings

Notes:

```text

```

### 2.4 Main Feature Requirements

Feature name:

```text

```

Required actions:

- [ ] Create
- [ ] Read list
- [ ] Read detail
- [ ] Update
- [ ] Delete
- [ ] Approve
- [ ] Reject
- [ ] Cancel
- [ ] Export
- [ ] Notify

Details:

```text

```

## 3. Non-Functional Requirements

- [ ] Responsive UI
- [ ] Basic security
- [ ] Error handling
- [ ] Loading states
- [ ] Empty states
- [ ] Form validation
- [ ] Beginner-friendly code structure
- [ ] Maintainable folder structure
- [ ] Production environment variables
- [ ] Deployment readiness

Notes:

```text

```

## 4. Permission Rules

| Action | Guest | User | Admin |
|---|---:|---:|---:|
| View public page | Yes | Yes | Yes |
| Sign up | Yes | No | No |
| Login | Yes | No | No |
| Logout | No | Yes | Yes |
| View own data | No | Yes | Yes |
| Create own data | No | Yes | Yes |
| Edit own data | No | Yes | Yes |
| Delete own data | No | Yes | Yes |
| View others' data | No | No | Yes |
| Edit others' data | No | No | Yes |
| Delete others' data | No | No | Yes |
| Admin action | No | No | Yes |

Custom permission rules:

| Action | Guest | User | Admin | Notes |
|---|---:|---:|---:|---|
|  |  |  |  |  |

## 5. Validation Rules

Define input validation rules.

| Field | Required | Rule | Error Message |
|---|---:|---|---|
| email | Yes | Valid email format | Invalid email address |
| password | Yes | Minimum length required | Password is too short |
| name | Yes | Not empty | Name is required |
| title | Yes | Not empty | Title is required |

Custom fields:

| Field | Required | Rule | Error Message |
|---|---:|---|---|
|  |  |  |  |

## 6. Error Handling

Common errors:

| Status | Meaning | Example |
|---:|---|---|
| 400 | Invalid request | Missing required field |
| 401 | Unauthorized | Login required |
| 403 | Forbidden | No permission |
| 404 | Not found | Data does not exist |
| 409 | Conflict | Duplicate data |
| 500 | Server error | Unexpected server error |

## 7. Screen Requirements

| Screen | User Role | Purpose | Main Action |
|---|---|---|---|
| Home | Guest/User | Main landing page | |
| Login | Guest | Login | |
| Sign Up | Guest | Create account | |
| Dashboard | User/Admin | Main workspace | |
| Admin Dashboard | Admin | Manage service | |

Custom screens:

| Screen | User Role | Purpose | Main Action |
|---|---|---|---|
|  |  |  |  |

## 8. Data Requirements

What data must be saved?

| Data | Description | Owner |
|---|---|---|
| User | Account information | User |
|  |  |  |

## 9. External Integrations

List external services if needed.

- [ ] Payment
- [ ] Email
- [ ] SMS
- [ ] Push notification
- [ ] Map
- [ ] File upload
- [ ] Analytics
- [ ] AI API

Notes:

```text

```