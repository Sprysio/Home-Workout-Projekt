# syntax=docker/dockerfile:1

# Build stage with Node
FROM node:25.9.0-alpine3.23@sha256:ad82ecad30371c43f4057aaa4800a8ed88f9446553a2d21323710c7b937177fc AS build
LABEL stage=builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --prefer-offline --no-audit
COPY . .
RUN npm run build

# Runtime stage with minimal Nginx
FROM nginx:1.28.3-alpine3.23@sha256:a8b39bd9cf0f83869a2162827a0caf6137ddf759d50a171451b335cecc87d236
LABEL maintainer="app-team" \
      version="1.0.0" \
      description="Frontend - React 19 SPA served via Nginx with API proxy"
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.default.conf.template /etc/nginx/templates/default.conf.template
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
