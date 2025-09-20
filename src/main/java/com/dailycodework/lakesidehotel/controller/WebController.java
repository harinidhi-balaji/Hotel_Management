package com.dailycodework.lakesidehotel.controller;

import com.dailycodework.lakesidehotel.dto.WebRoomDTO;
import com.dailycodework.lakesidehotel.model.BookedRoom;
import com.dailycodework.lakesidehotel.model.Room;
import com.dailycodework.lakesidehotel.model.User;
import com.dailycodework.lakesidehotel.service.IUserService;
import com.dailycodework.lakesidehotel.service.IRoomService;
import com.dailycodework.lakesidehotel.service.IBookingService;
import com.dailycodework.lakesidehotel.service.RoomImageUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final IUserService userService;
    private final IRoomService roomService;
    private final IBookingService bookingService;
    private final RoomImageUpdateService roomImageUpdateService;

    @GetMapping("/")
    public String home(Model model) {
        List<Room> rooms = roomService.getAllRooms();
        model.addAttribute("rooms", convertToWebRoomDTOs(rooms));
        return "index";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/rooms")
    public String rooms(Model model) {
        List<Room> rooms = roomService.getAllRooms();
        model.addAttribute("rooms", convertToWebRoomDTOs(rooms));
        model.addAttribute("roomTypes", roomService.getAllRoomTypes());
        return "rooms";
    }

    @GetMapping("/room/{roomId}")
    public String roomDetails(@PathVariable Long roomId, Model model) {
        model.addAttribute("room", roomService.getRoomById(roomId).orElse(null));
        return "room-details";
    }

    @GetMapping("/booking/success")
    public String bookingSuccess(Model model) {
        return "booking-success";
    }

    @GetMapping("/bookings")
    public String myBookings(Model model, Authentication authentication) {
        if (authentication != null) {
            // For authenticated users, show their specific bookings
            String email = authentication.getName();
            model.addAttribute("bookings", bookingService.getBookingsByUserEmail(email));
        } else {
            // For guest users, show recent bookings (last 10) to help them find their
            // booking
            List<BookedRoom> allBookings = bookingService.getAllBookings();
            // Get last 10 bookings ordered by booking ID (most recent first)
            List<BookedRoom> recentBookings = allBookings.stream()
                    .sorted((b1, b2) -> Long.compare(b2.getBookingId(), b1.getBookingId()))
                    .limit(10)
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("bookings", recentBookings);
        }
        model.addAttribute("isAuthenticated", authentication != null);
        return "bookings";
    }

    @GetMapping("/booking/new")
    public String selectRoomForBooking() {
        return "redirect:/rooms";
    }

    @GetMapping("/booking/new/{roomId}")
    public String newBooking(@PathVariable Long roomId, Model model) {
        model.addAttribute("room", roomService.getRoomById(roomId).orElse(null));
        return "booking-form";
    }

    @PostMapping("/booking/room/{roomId}/book")
    public String processBooking(@PathVariable Long roomId,
            @RequestParam(required = false) String guestFullName,
            @RequestParam(required = false) String guestEmail,
            @RequestParam(required = false) String checkInDate,
            @RequestParam(required = false) String checkOutDate,
            @RequestParam(required = false) Integer numOfAdults,
            @RequestParam(required = false) Integer numOfChildren,
            RedirectAttributes redirectAttributes) {
        try {
            // Validation
            if (guestFullName == null || guestFullName.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Guest name is required");
                return "redirect:/booking/new/" + roomId;
            }
            if (guestEmail == null || guestEmail.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Guest email is required");
                return "redirect:/booking/new/" + roomId;
            }
            if (checkInDate == null || checkOutDate == null) {
                redirectAttributes.addFlashAttribute("error", "Check-in and check-out dates are required");
                return "redirect:/booking/new/" + roomId;
            }
            if (numOfAdults == null || numOfAdults < 1) {
                numOfAdults = 1; // Default value
            }
            if (numOfChildren == null) {
                numOfChildren = 0; // Default value
            }

            BookedRoom booking = new BookedRoom();
            booking.setGuestFullName(guestFullName.trim());
            booking.setGuestEmail(guestEmail.trim());
            booking.setCheckInDate(java.time.LocalDate.parse(checkInDate));
            booking.setCheckOutDate(java.time.LocalDate.parse(checkOutDate));
            booking.setNumOfAdults(numOfAdults);
            booking.setNumOfChildren(numOfChildren);

            String confirmationCode = bookingService.saveBooking(roomId, booking);

            // Store booking details in session for the success page
            redirectAttributes.addFlashAttribute("confirmationCode", confirmationCode);
            redirectAttributes.addFlashAttribute("guestName", guestFullName.trim());
            redirectAttributes.addFlashAttribute("guestEmail", guestEmail.trim());
            redirectAttributes.addFlashAttribute("checkInDate", checkInDate);
            redirectAttributes.addFlashAttribute("checkOutDate", checkOutDate);
            redirectAttributes.addFlashAttribute("numOfAdults", numOfAdults);
            redirectAttributes.addFlashAttribute("numOfChildren", numOfChildren);
            redirectAttributes.addFlashAttribute("roomId", roomId);

            return "redirect:/booking/success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Booking failed: " + e.getMessage());
            return "redirect:/booking/new/" + roomId;
        }
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("totalRooms", roomService.getAllRooms().size());
        model.addAttribute("totalBookings", bookingService.getAllBookings().size());
        model.addAttribute("totalUsers", userService.getUsers().size());
        return "admin/dashboard";
    }

    @GetMapping("/admin/rooms")
    public String adminRooms(Model model) {
        List<Room> rooms = roomService.getAllRooms();
        model.addAttribute("rooms", convertToWebRoomDTOs(rooms));
        return "admin/rooms";
    }

    @GetMapping("/admin/bookings")
    public String adminBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin/bookings";
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        model.addAttribute("users", userService.getUsers());
        return "admin/users";
    }

    @PostMapping("/admin/regenerate-images")
    public String regenerateRoomImages(RedirectAttributes redirectAttributes) {
        try {
            roomImageUpdateService.updateAllRoomImages();
            redirectAttributes.addFlashAttribute("success", "All room images have been regenerated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error regenerating images: " + e.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        if (authentication != null) {
            String email = authentication.getName();
            model.addAttribute("user", userService.getUser(email));
        }
        return "profile";
    }

    /**
     * Convert Room entities to WebRoomDTO with base64-encoded images for web
     * templates
     */
    private List<WebRoomDTO> convertToWebRoomDTOs(List<Room> rooms) {
        List<WebRoomDTO> webRoomDTOs = new ArrayList<>();
        for (Room room : rooms) {
            WebRoomDTO dto = new WebRoomDTO(room.getId(), room.getRoomType(), room.getRoomPrice());
            dto.setBooked(room.isBooked());

            // Convert Blob image to base64 string
            if (room.getPhoto() != null) {
                try {
                    byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
                    if (photoBytes != null && photoBytes.length > 0) {
                        String base64Photo = Base64.getEncoder().encodeToString(photoBytes);
                        dto.setPhoto(base64Photo);
                    }
                } catch (SQLException e) {
                    // Log error and continue without photo
                    System.err.println("Error retrieving photo for room " + room.getId() + ": " + e.getMessage());
                }
            }
            webRoomDTOs.add(dto);
        }
        return webRoomDTOs;
    }
}
