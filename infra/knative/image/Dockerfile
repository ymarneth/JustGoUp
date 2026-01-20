# ===== Stage 1: Build the Go binary =====
FROM golang:1.21-alpine AS builder

# Install git (required to fetch modules)
RUN apk add --no-cache git

# Set working directory
WORKDIR /app

# Copy go.mod and go.sum first to leverage caching
COPY go.mod go.sum ./
RUN go mod download

# Copy the rest of the source code
COPY . .

# Build a statically-linked binary
RUN CGO_ENABLED=0 GOOS=linux go build -o ingestion-service main.go

# ===== Stage 2: Minimal runtime image =====
FROM alpine:latest

# Add certificates for HTTPS if needed
RUN apk add --no-cache ca-certificates