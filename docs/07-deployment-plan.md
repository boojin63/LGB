# Deployment Plan Template

## 1. Deployment Goal

Describe the deployment goal.

```text

```

## 2. Deployment Architecture

Recommended structure:

| Part | Service | Notes |
|---|---|---|
| Frontend |  |  |
| Backend |  |  |
| Database |  |  |
| File Storage |  |  |
| Domain |  |  |

Example options:

| Part | Beginner-Friendly Options |
|---|---|
| Frontend | Vercel, Netlify |
| Backend | Render, Railway |
| Database | Railway MySQL, PlanetScale, Supabase |
| File Storage | S3-compatible storage, Supabase Storage |
| Domain | Namecheap, Cloudflare, Gabia |

## 3. Environment Variables

### Frontend

```env
VITE_API_URL=
EXPO_PUBLIC_API_URL=
NEXT_PUBLIC_API_URL=
```

### Backend

```env
DATABASE_URL=
JWT_SECRET=
PORT=
NODE_ENV=
CORS_ORIGIN=
```

### Optional

```env
EMAIL_HOST=
EMAIL_USER=
EMAIL_PASSWORD=
PAYMENT_SECRET_KEY=
STORAGE_ACCESS_KEY=
STORAGE_SECRET_KEY=
```

## 4. Build Commands

### Frontend

```bash
npm install
npm run build
```

### Backend

```bash
npm install
npm run build
npm start
```

### Prisma

```bash
npx prisma generate
npx prisma migrate deploy
```

## 5. Local Pre-Deployment Checklist

- [ ] Frontend runs locally.
- [ ] Backend runs locally.
- [ ] Database connects locally.
- [ ] Login works locally.
- [ ] Main feature works locally.
- [ ] No console errors.
- [ ] No server errors.
- [ ] Environment variables are not hardcoded.
- [ ] `.env` is not committed.
- [ ] Build command works.

## 6. Frontend Deployment Checklist

- [ ] Repository connected to hosting service.
- [ ] Build command configured.
- [ ] Output directory configured.
- [ ] API URL environment variable configured.
- [ ] Production build succeeds.
- [ ] Frontend page loads.
- [ ] Frontend can call backend API.

## 7. Backend Deployment Checklist

- [ ] Repository connected to hosting service.
- [ ] Build command configured.
- [ ] Start command configured.
- [ ] Environment variables configured.
- [ ] Database URL configured.
- [ ] CORS configured.
- [ ] Health check endpoint works.
- [ ] Logs are accessible.

## 8. Database Deployment Checklist

- [ ] Production database created.
- [ ] DATABASE_URL copied correctly.
- [ ] Prisma generate works.
- [ ] Prisma migration applied.
- [ ] Seed data added if needed.
- [ ] Database backup plan considered.

## 9. Production Verification

After deployment, test:

- [ ] Home page loads.
- [ ] Sign up works.
- [ ] Login works.
- [ ] Logout works.
- [ ] Protected page works.
- [ ] Admin permission works.
- [ ] Main feature create works.
- [ ] Main feature list works.
- [ ] Main feature detail works.
- [ ] Main feature update works.
- [ ] Main feature delete works.
- [ ] Error states work.

## 10. Rollback Plan

If deployment fails:

1. Check build logs.
2. Check environment variables.
3. Check database connection.
4. Check CORS configuration.
5. Revert latest commit if needed.
6. Restore previous deployment if hosting supports rollback.
7. Restore database backup if needed.

## 11. Deployment Notes

Write deployment-specific notes here.

```text

```

## 12. Final Deployment Status

Status:

```text
READY / NOT READY / DEPLOYED / FAILED
```

Notes:

```text

```