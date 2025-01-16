# Stage 1: Build the frontend
FROM node:20 AS builder

WORKDIR /app

# Set environment variables
ENV API_URL=http://77.37.125.190:8087/api

# Copy package.json and install dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the application code
COPY . .

# Build the frontend
RUN npm run build --prod

# Stage 2: Serve the frontend with Nginx
FROM nginx:alpine

# Copy the built frontend files to /usr/share/nginx/html/browser
COPY --from=builder /app/dist/frontend /usr/share/nginx/html/browser

# Copy the custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
