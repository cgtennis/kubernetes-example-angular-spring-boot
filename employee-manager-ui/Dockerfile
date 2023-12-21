# First Stage: Build with Node.js
FROM node:16.16 AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install -f
COPY . .
RUN npm run build

# Second Stage: Setup Nginx
FROM nginx:1.17.1-alpine
# Copy Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copy built files from the first stage
COPY --from=builder /app/dist/employeemanagerapp/* /usr/share/nginx/html/
