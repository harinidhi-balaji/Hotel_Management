#!/bin/bash

# LakeSide Hotel - Quick Start Script
# This script builds and runs the LakeSide Hotel application

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}     LakeSide Hotel - Quick Start      ${NC}"
    echo -e "${BLUE}========================================${NC}"
}

# Function to check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 17 or higher."
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1-2)
    print_status "Java version: $JAVA_VERSION"
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven 3.6 or higher."
        exit 1
    fi
    
    MVN_VERSION=$(mvn -version 2>&1 | head -n 1 | cut -d' ' -f3)
    print_status "Maven version: $MVN_VERSION"
    
    print_status "Prerequisites check passed!"
}

# Function to clean previous processes
cleanup_previous() {
    print_status "Cleaning up previous instances..."
    
    # Kill any process running on port 8080
    if lsof -ti:8080 >/dev/null 2>&1; then
        print_warning "Port 8080 is in use. Killing existing processes..."
        lsof -ti:8080 | xargs kill -9 2>/dev/null || true
        sleep 2
    fi
    
    print_status "Cleanup completed!"
}

# Function to build the application
build_application() {
    print_status "Building the application..."
    
    # Clean and build with tests skipped
    mvn clean package -DskipTests -q
    
    if [ $? -eq 0 ]; then
        print_status "Build completed successfully!"
        print_status "JAR file: target/lakeSide-hotel-0.0.1-SNAPSHOT.jar"
    else
        print_error "Build failed!"
        exit 1
    fi
}

# Function to start the application
start_application() {
    local run_mode=$1
    
    print_status "Starting the application in $run_mode mode..."
    
    if [ "$run_mode" = "development" ]; then
        print_status "Starting with Maven (hot reload enabled)..."
        print_warning "Press Ctrl+C to stop the application"
        mvn spring-boot:run
    elif [ "$run_mode" = "production" ]; then
        print_status "Starting JAR file..."
        java -jar target/lakeSide-hotel-0.0.1-SNAPSHOT.jar
    elif [ "$run_mode" = "background" ]; then
        print_status "Starting in background mode..."
        nohup java -jar target/lakeSide-hotel-0.0.1-SNAPSHOT.jar > application-$(date +%Y%m%d-%H%M%S).log 2>&1 &
        local PID=$!
        print_status "Application started in background with PID: $PID"
        print_status "Log file: application-$(date +%Y%m%d-%H%M%S).log"
        sleep 5
        if kill -0 $PID 2>/dev/null; then
            print_status "Application is running successfully!"
        else
            print_error "Application failed to start. Check the log file."
            exit 1
        fi
    fi
}

# Function to test the application
test_application() {
    print_status "Testing application connectivity..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:8080/ >/dev/null 2>&1; then
            print_status "Application is responding at http://localhost:8080/"
            break
        else
            if [ $attempt -eq $max_attempts ]; then
                print_error "Application failed to respond after $max_attempts attempts"
                return 1
            fi
            print_status "Waiting for application to start... (attempt $attempt/$max_attempts)"
            sleep 2
            ((attempt++))
        fi
    done
    
    print_status "Application URLs:"
    echo "  • Homepage:     http://localhost:8080/"
    echo "  • All Rooms:    http://localhost:8080/rooms"
    echo "  • My Bookings:  http://localhost:8080/bookings"
    echo "  • Login:        http://localhost:8080/login"
    echo "  • Admin Panel:  http://localhost:8080/admin"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help              Show this help message"
    echo "  -m, --mode MODE         Run mode: development|production|background (default: development)"
    echo "  -s, --skip-build        Skip the build step"
    echo "  -t, --test-only         Only test if application is running"
    echo "  -c, --cleanup-only      Only cleanup previous instances"
    echo ""
    echo "Examples:"
    echo "  $0                      # Build and run in development mode"
    echo "  $0 -m production        # Build and run in production mode"
    echo "  $0 -m background        # Build and run in background"
    echo "  $0 -s -m production     # Skip build, run existing JAR"
    echo "  $0 -t                   # Test if application is responding"
    echo "  $0 -c                   # Cleanup previous instances only"
}

# Main execution
main() {
    local run_mode="development"
    local skip_build=false
    local test_only=false
    local cleanup_only=false
    
    # Parse command line arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_usage
                exit 0
                ;;
            -m|--mode)
                run_mode="$2"
                shift 2
                ;;
            -s|--skip-build)
                skip_build=true
                shift
                ;;
            -t|--test-only)
                test_only=true
                shift
                ;;
            -c|--cleanup-only)
                cleanup_only=true
                shift
                ;;
            *)
                print_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
    
    # Validate run mode
    if [[ "$run_mode" != "development" && "$run_mode" != "production" && "$run_mode" != "background" ]]; then
        print_error "Invalid run mode: $run_mode. Must be: development, production, or background"
        exit 1
    fi
    
    print_header
    
    # Execute based on options
    if [ "$cleanup_only" = true ]; then
        cleanup_previous
        print_status "Cleanup completed!"
        exit 0
    fi
    
    if [ "$test_only" = true ]; then
        test_application
        exit 0
    fi
    
    check_prerequisites
    cleanup_previous
    
    if [ "$skip_build" = false ]; then
        build_application
    else
        print_warning "Skipping build step as requested"
    fi
    
    if [ "$run_mode" = "background" ]; then
        start_application "$run_mode"
        sleep 3
        test_application
    else
        start_application "$run_mode"
    fi
}

# Run main function with all arguments
main "$@"