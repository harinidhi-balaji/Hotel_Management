package com.dailycodework.lakesidehotel.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utility class for generating room images
 */
public class RoomImageGenerator {

    public static byte[] generateRoomImage(String roomType, String price, int roomNumber) throws IOException {
        // Create a 400x300 image
        int width = 400;
        int height = 300;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set rendering hints for better quality
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background gradient
        GradientPaint gradient = new GradientPaint(0, 0, Color.decode("#f8f9fa"), 0, height, Color.decode("#e9ecef"));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);

        // Border
        g2d.setColor(Color.decode("#2c3e50"));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(5, 5, width - 10, height - 10);

        // Room number background
        g2d.setColor(Color.decode("#3498db"));
        g2d.fillRoundRect(20, 20, 80, 40, 10, 10);

        // Room number text
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String roomText = "Room " + roomNumber;
        int x = 20 + (80 - fm.stringWidth(roomText)) / 2;
        int y = 20 + ((40 - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(roomText, x, y);

        // Room type
        g2d.setColor(Color.decode("#2c3e50"));
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        fm = g2d.getFontMetrics();
        x = (width - fm.stringWidth(roomType)) / 2;
        y = 120;
        g2d.drawString(roomType, x, y);

        // Price
        g2d.setColor(Color.decode("#e74c3c"));
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        fm = g2d.getFontMetrics();
        String priceText = "$" + price + " per night";
        x = (width - fm.stringWidth(priceText)) / 2;
        y = 160;
        g2d.drawString(priceText, x, y);

        // Hotel name
        g2d.setColor(Color.decode("#95a5a6"));
        g2d.setFont(new Font("Arial", Font.ITALIC, 14));
        fm = g2d.getFontMetrics();
        String hotelName = "LakeSide Hotel";
        x = (width - fm.stringWidth(hotelName)) / 2;
        y = 200;
        g2d.drawString(hotelName, x, y);

        // Amenities
        g2d.setColor(Color.decode("#7f8c8d"));
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        String amenities = "★ WiFi ★ AC ★ Room Service ★ TV";
        fm = g2d.getFontMetrics();
        x = (width - fm.stringWidth(amenities)) / 2;
        y = 230;
        g2d.drawString(amenities, x, y);

        // Decorative elements (simple geometric shapes)
        g2d.setColor(Color.decode("#bdc3c7"));
        g2d.fillOval(width - 60, height - 60, 40, 40);
        g2d.fillOval(20, height - 60, 40, 40);

        g2d.dispose();

        // Convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}