package com.dailycodework.lakesidehotel.service;

import com.dailycodework.lakesidehotel.model.Room;
import com.dailycodework.lakesidehotel.repository.RoomRepository;
import com.dailycodework.lakesidehotel.util.RoomImageGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.rowset.serial.SerialBlob;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.List;

/**
 * Data initialization service to populate sample hotel rooms
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final RoomRepository roomRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roomRepository.count() < 15) {
            log.info("Initializing sample room data...");
            createSampleRooms();
            log.info("Sample room data initialization completed!");
        } else {
            log.info("Room data already exists, checking for dummy images...");
            updateDummyImages();
        }
    }

    private void createSampleRooms() {
        try {
            // Room 1: Single Standard
            createRoom("Single Standard", new BigDecimal("89.99"),
                    RoomImageGenerator.generateRoomImage("Single Standard", "89.99", 1));

            // Room 2: Double Deluxe
            createRoom("Double Deluxe", new BigDecimal("129.99"),
                    RoomImageGenerator.generateRoomImage("Double Deluxe", "129.99", 2));

            // Room 3: King Suite
            createRoom("King Suite", new BigDecimal("199.99"),
                    RoomImageGenerator.generateRoomImage("King Suite", "199.99", 3));

            // Room 4: Twin Standard
            createRoom("Twin Standard", new BigDecimal("99.99"),
                    RoomImageGenerator.generateRoomImage("Twin Standard", "99.99", 4));

            // Room 5: Queen Premium
            createRoom("Queen Premium", new BigDecimal("159.99"),
                    RoomImageGenerator.generateRoomImage("Queen Premium", "159.99", 5));

            // Room 6: Presidential Suite
            createRoom("Presidential Suite", new BigDecimal("399.99"),
                    RoomImageGenerator.generateRoomImage("Presidential Suite", "399.99", 6));

            // Room 7: Single Executive
            createRoom("Single Executive", new BigDecimal("119.99"),
                    RoomImageGenerator.generateRoomImage("Single Executive", "119.99", 7));

            // Room 8: Double Ocean View
            createRoom("Double Ocean View", new BigDecimal("179.99"),
                    RoomImageGenerator.generateRoomImage("Double Ocean View", "179.99", 8));

            // Room 9: Family Suite
            createRoom("Family Suite", new BigDecimal("249.99"),
                    RoomImageGenerator.generateRoomImage("Family Suite", "249.99", 9));

            // Room 10: Honeymoon Suite
            createRoom("Honeymoon Suite", new BigDecimal("299.99"),
                    RoomImageGenerator.generateRoomImage("Honeymoon Suite", "299.99", 10));

            // Room 11: Standard Triple
            createRoom("Standard Triple", new BigDecimal("139.99"),
                    RoomImageGenerator.generateRoomImage("Standard Triple", "139.99", 11));

            // Room 12: Penthouse
            createRoom("Penthouse", new BigDecimal("599.99"),
                    RoomImageGenerator.generateRoomImage("Penthouse", "599.99", 12));

            // Room 13: Single Economy
            createRoom("Single Economy", new BigDecimal("69.99"),
                    RoomImageGenerator.generateRoomImage("Single Economy", "69.99", 13));

            // Room 14: Double Superior
            createRoom("Double Superior", new BigDecimal("149.99"),
                    RoomImageGenerator.generateRoomImage("Double Superior", "149.99", 14));

            // Room 15: Junior Suite
            createRoom("Junior Suite", new BigDecimal("219.99"),
                    RoomImageGenerator.generateRoomImage("Junior Suite", "219.99", 15));

        } catch (Exception e) {
            log.error("Error creating sample rooms: ", e);
        }
    }

    private void createRoom(String roomType, BigDecimal price, byte[] imageData) throws Exception {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(price);

        if (imageData != null) {
            Blob photoBlob = new SerialBlob(imageData);
            room.setPhoto(photoBlob);
        }

        roomRepository.save(room);
        log.info("Created room: {} - ${}", roomType, price);
    }

    /**
     * Update existing rooms that have dummy image data with generated images
     */
    private void updateDummyImages() {
        try {
            List<Room> allRooms = roomRepository.findAll();
            int updatedCount = 0;
            int roomCounter = 1;

            for (Room room : allRooms) {
                boolean needsUpdate = false;

                // Check if room has dummy image data
                if (room.getPhoto() != null) {
                    try {
                        Blob photoBlob = room.getPhoto();
                        byte[] photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
                        String photoString = new String(photoBytes);

                        // Check if it contains dummy data
                        if ("dummy_photo_data".equals(photoString)) {
                            needsUpdate = true;
                            log.info("Found room {} with dummy image data: {}", room.getId(), room.getRoomType());
                        }
                    } catch (Exception e) {
                        log.warn("Could not check image data for room {}: {}", room.getId(), e.getMessage());
                    }
                } else {
                    // Room has no image at all
                    needsUpdate = true;
                    log.info("Found room {} with no image: {}", room.getId(), room.getRoomType());
                }

                if (needsUpdate) {
                    try {
                        // Generate new image
                        byte[] newImageData = RoomImageGenerator.generateRoomImage(
                                room.getRoomType(),
                                room.getRoomPrice().toString(),
                                roomCounter);

                        if (newImageData != null) {
                            Blob photoBlob = new SerialBlob(newImageData);
                            room.setPhoto(photoBlob);
                            roomRepository.save(room);
                            updatedCount++;
                            log.info("Updated image for room {} - {}", room.getId(), room.getRoomType());
                        }
                    } catch (Exception e) {
                        log.error("Error updating image for room {} - {}: {}", room.getId(), room.getRoomType(),
                                e.getMessage());
                    }
                }
                roomCounter++;
            }

            if (updatedCount > 0) {
                log.info("Updated images for {} rooms with dummy data", updatedCount);
            } else {
                log.info("No rooms found with dummy image data");
            }

        } catch (Exception e) {
            log.error("Error during dummy image update: ", e);
        }
    }
}