package com.dailycodework.lakesidehotel.service;

import com.dailycodework.lakesidehotel.model.Room;
import com.dailycodework.lakesidehotel.repository.RoomRepository;
import com.dailycodework.lakesidehotel.util.RoomImageGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.util.List;

/**
 * Service to regenerate images for existing rooms
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomImageUpdateService {

    private final RoomRepository roomRepository;

    /**
     * Update all existing rooms with generated images
     */
    public void updateAllRoomImages() {
        List<Room> rooms = roomRepository.findAll();
        log.info("Updating images for {} rooms...", rooms.size());

        int counter = 1;
        for (Room room : rooms) {
            try {
                // Generate image for this room
                byte[] imageData = RoomImageGenerator.generateRoomImage(
                        room.getRoomType(),
                        room.getRoomPrice().toString(),
                        counter);

                if (imageData != null) {
                    Blob photoBlob = new SerialBlob(imageData);
                    room.setPhoto(photoBlob);
                    roomRepository.save(room);
                    log.info("Updated image for room {} - {}", room.getId(), room.getRoomType());
                } else {
                    log.warn("Failed to generate image for room {} - {}", room.getId(), room.getRoomType());
                }

                counter++;

            } catch (Exception e) {
                log.error("Error updating image for room {} - {}: {}", room.getId(), room.getRoomType(),
                        e.getMessage());
            }
        }

        log.info("Completed updating images for all rooms!");
    }

    /**
     * Update image for a specific room
     */
    public void updateRoomImage(Long roomId) {
        roomRepository.findById(roomId).ifPresentOrElse(room -> {
            try {
                byte[] imageData = RoomImageGenerator.generateRoomImage(
                        room.getRoomType(),
                        room.getRoomPrice().toString(),
                        roomId.intValue());

                if (imageData != null) {
                    Blob photoBlob = new SerialBlob(imageData);
                    room.setPhoto(photoBlob);
                    roomRepository.save(room);
                    log.info("Updated image for room {} - {}", room.getId(), room.getRoomType());
                } else {
                    log.warn("Failed to generate image for room {} - {}", room.getId(), room.getRoomType());
                }

            } catch (Exception e) {
                log.error("Error updating image for room {} - {}: {}", room.getId(), room.getRoomType(),
                        e.getMessage());
            }
        }, () -> log.warn("Room with ID {} not found", roomId));
    }
}