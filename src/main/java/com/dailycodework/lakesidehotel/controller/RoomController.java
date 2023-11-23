package com.dailycodework.lakesidehotel.controller;

import com.dailycodework.lakesidehotel.exception.PhotoRetrievalExcetion;
import com.dailycodework.lakesidehotel.exception.ResourceNotFoundException;
import com.dailycodework.lakesidehotel.model.BookedRoom;
import com.dailycodework.lakesidehotel.model.Room;
import com.dailycodework.lakesidehotel.response.BookingResponse;
import com.dailycodework.lakesidehotel.response.RoomResponse;
import com.dailycodework.lakesidehotel.service.BookingService;
import com.dailycodework.lakesidehotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
    private final IRoomService roomService;
    private final BookingService bookingService;



@PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(
        @RequestParam("photo") MultipartFile photo,
        @RequestParam("roomType") String roomType,
        @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {
      Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
      RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(),
              savedRoom.getRoomPrice());
      return ResponseEntity.ok(response);
    }

    @GetMapping("/room/types")
    public List<String> getRoomTypes(){
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException, ResourceNotFoundException {
    List<Room> rooms = roomService.getAllRooms();
    List<RoomResponse> roomResponses = new ArrayList<>();
    for(Room room : rooms){
        byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
        if(photoBytes != null && photoBytes.length > 0){
            String base64Photo = Base64.encodeBase64String(photoBytes);
            RoomResponse roomResponse = getRoomResponse(room);
            roomResponse.setPhoto(base64Photo);
            roomResponses.add(roomResponse);
        }
    }
    return ResponseEntity.ok(roomResponses);
    }

    private RoomResponse getRoomResponse(Room room) {
    List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
//    List<BookingResponse> bookingInfo = bookings
//            .stream()
//            .map(booking -> new BookingResponse(booking.getBookingId(),
//                    booking.getCheckInDate(),
//                    booking.getCheckOutDate(), booking.getBookingConfirmationCode())).toList();
        byte [] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if(photoBlob != null){
            try{
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            }catch (SQLException e){
                throw new PhotoRetrievalExcetion("Error retrieving photo");
            }
        }
        return new RoomResponse(room.getId(),
                room.getRoomType(), room.getRoomPrice(),
                room.isBooked(), photoBytes);
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingService.getAllBookingsByRoomId(roomId);
    }

}
