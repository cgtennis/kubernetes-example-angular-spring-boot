# First Stage: Build with Node.js
FROM node:16.16-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# Build the Angular application with a build argument for apiBaseUrl
ARG API_BASE_URL=$API_BASE_URL
RUN echo "****************************************************"
RUN echo "API_BASE_URL: ${API_BASE_URL}"
RUN echo "****************************************************"
RUN npm run build  

# Second Stage: Setup Nginx
FROM nginx:1.17.1-alpine
# Copy Nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf
# Copy built files from the first stage
COPY --from=builder /app/dist/employeemanagerapp/* /usr/share/nginx/html/
