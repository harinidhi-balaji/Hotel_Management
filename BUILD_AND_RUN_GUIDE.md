# LakeSide Hotel Application - Build and Run Guide

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Build Commands](#build-commands)
- [Run Commands](#run-commands)
- [Application Status](#application-status)
- [Troubleshooting](#troubleshooting)
- [Application Features](#application-features)

---

## ✅ Prerequisites

Before building and running the application, ensure you have:

1. **Java 17 or higher**

   ```bash
   java -version
   ```

2. **Maven 3.6 or higher**

   ```bash
   mvn -version
   ```

3. **MySQL Database** running on localhost:3306

   - Database name: `lakeSideHotel`
   - User: `root`
   - Password: `password` (or update in `application.properties`)

4. **Git** (for version control)

---

## 🚀 Quick Start

### 1. Navigate to Project Directory

```bash
cd "/Users/hariparthu/Hari Documents/JAVA/pro 2/lakeSide-hotel-demo-server-master"
```

### 2. Clean Build and Run

```bash
# Clean build with tests skipped
mvn clean package -DskipTests

# Run the application
mvn spring-boot:run
```

**OR** run the JAR directly:

```bash
# Build the JAR
mvn clean package -DskipTests

# Run the JAR
java -jar target/lakeSide-hotel-0.0.1-SNAPSHOT.jar
```

---

## 🔨 Build Commands

### Full Clean Build

```bash
# Complete clean and compile
mvn clean compile

# Build with package (creates JAR)
mvn clean package

# Build skipping tests (faster)
mvn clean package -DskipTests
```

### Compile Only

```bash
# Just compile source code
mvn compile

# Compile with quiet output
mvn compile -q
```

### Verify Build

```bash
# Check if JAR was created
ls -la target/*.jar
```

---

## ▶️ Run Commands

### Method 1: Maven Spring Boot Plugin (Recommended for Development)

```bash
# Run with Maven (includes hot reload)
mvn spring-boot:run

# Run with profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with specific port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Method 2: Executable JAR (Recommended for Production)

```bash
# First build the JAR
mvn clean package -DskipTests

# Run the JAR
java -jar target/lakeSide-hotel-0.0.1-SNAPSHOT.jar

# Run with specific profile
java -jar target/lakeSide-hotel-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Run with custom port
java -jar target/lakeSide-hotel-0.0.1-SNAPSHOT.jar --server.port=8081
```

### Method 3: Background Execution

```bash
# Run in background with output to file
nohup mvn spring-boot:run > application.log 2>&1 &

# Or with JAR
nohup java -jar target/lakeSide-hotel-0.0.1-SNAPSHOT.jar > application.log 2>&1 &

# Check if running
ps aux | grep java
```

---

## 📊 Application Status

### Check if Application is Running

```bash
# Test homepage
curl http://localhost:8080/

# Check application health (if actuator is enabled)
curl http://localhost:8080/actuator/health

# Check what's running on port 8080
lsof -ti:8080
```

### Access the Application

- **Homepage**: http://localhost:8080/
- **All Rooms**: http://localhost:8080/rooms
- **My Bookings**: http://localhost:8080/bookings
- **Admin Panel**: http://localhost:8080/admin
- **Login**: http://localhost:8080/login
- **Register**: http://localhost:8080/register

### Stop the Application

```bash
# If running in foreground: Ctrl+C

# If running in background:
# Find the process ID
ps aux | grep lakeSide-hotel

# Kill by PID (replace XXXX with actual PID)
kill XXXX

# Or kill all Java processes on port 8080
lsof -ti:8080 | xargs kill -9
```

---

## 🛠️ Troubleshooting

### Common Issues and Solutions

#### 1. Port 8080 Already in Use

```bash
# Find what's using port 8080
lsof -ti:8080

# Kill the process
lsof -ti:8080 | xargs kill -9

# Or run on different port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

#### 2. Database Connection Issues

- Ensure MySQL is running
- Check database credentials in `src/main/resources/application.properties`
- Create database if it doesn't exist:
  ```sql
  CREATE DATABASE lakeSideHotel;
  ```

#### 3. Build Failures

```bash
# Clean Maven cache
mvn clean

# Force update dependencies
mvn clean compile -U

# Skip tests if they're failing
mvn clean package -DskipTests
```

#### 4. Out of Memory

```bash
# Run with more memory
MAVEN_OPTS="-Xmx1024m" mvn spring-boot:run

# Or for JAR
java -Xmx1024m -jar target/lakeSide-hotel-0.0.1-SNAPSHOT.jar
```

### View Logs

```bash
# If running with output to file
tail -f application.log

# View startup logs
head -50 application.log

# View recent errors
grep -i error application.log | tail -10
```

---

## 🏨 Application Features

### ✅ Implemented Features

1. **Room Management**

   - View all available rooms with generated images
   - Room filtering and search
   - Room booking functionality

2. **Booking System**

   - Create new bookings
   - View booking history ("My Bookings")
   - **Cancel bookings** (✅ **FIXED** - Cancel button now works!)
   - Booking confirmation codes

3. **User Management**

   - User registration and login
   - Profile management
   - Role-based access (Admin/User)

4. **Admin Panel**

   - Manage rooms
   - View all bookings
   - User management

5. **Image System**
   - Automatic image generation for rooms
   - Dummy image detection and replacement
   - Base64 encoded images for web display

### 🔧 Technical Stack

- **Backend**: Spring Boot 3.1.4
- **Database**: MySQL with JPA/Hibernate
- **Frontend**: Thymeleaf templates with Bootstrap
- **Security**: Spring Security with JWT
- **Build Tool**: Maven
- **Java Version**: 17+

---

## 📁 Project Structure

```
lakeSide-hotel-demo-server-master/
├── src/main/java/com/dailycodework/lakesidehotel/
│   ├── controller/          # REST controllers
│   ├── service/            # Business logic
│   ├── model/              # Entity classes
│   ├── repository/         # Data access
│   ├── security/           # Security configuration
│   └── util/               # Utility classes
├── src/main/resources/
│   ├── templates/          # Thymeleaf HTML templates
│   ├── static/            # CSS, JS, images
│   └── application.properties
├── target/                 # Build output
├── pom.xml                # Maven configuration
└── *.log                  # Application logs
```

---

## 🎯 Development Tips

### Hot Reload

When running with `mvn spring-boot:run`, the application supports hot reload for:

- Java code changes (after recompilation)
- Template changes (immediate)
- Static resource changes (immediate)

### Database Schema

The application uses JPA auto-DDL to create tables automatically. On first run, it will:

1. Create necessary tables
2. Initialize sample room data
3. Generate images for rooms without photos

### Testing APIs

```bash
# Test room booking API
curl -X POST "http://localhost:8080/bookings/room/1/booking" \
  -H "Content-Type: application/json" \
  -d '{
    "checkInDate": "2025-09-25",
    "checkOutDate": "2025-09-27",
    "guestFullName": "Test User",
    "guestEmail": "test@example.com",
    "numOfAdults": 2,
    "numOfChildren": 0
  }'

# Test booking cancellation (use actual confirmation code)
curl -X DELETE "http://localhost:8080/bookings/confirmation/YOUR_CONFIRMATION_CODE"
```

---

## 📝 Build Information

**Last Build**: 2025-09-18T00:06:19+05:30  
**Build Status**: ✅ SUCCESS  
**JAR Location**: `target/lakeSide-hotel-0.0.1-SNAPSHOT.jar`  
**Application Port**: 8080  
**Database**: MySQL (lakeSideHotel)

**Recent Changes**:

- ✅ Fixed booking cancellation functionality
- ✅ Added cancel by confirmation code endpoint
- ✅ Enhanced booking service with new methods
- ✅ Updated frontend JavaScript for proper API calls
- ✅ Image generation system working properly

---

## 📞 Support

For issues or questions:

1. Check the troubleshooting section above
2. Review application logs in `*.log` files
3. Verify database connectivity
4. Ensure all prerequisites are installed

**Happy Coding!** 🚀
