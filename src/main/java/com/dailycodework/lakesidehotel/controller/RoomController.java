package com.dailycodework.lakesidehotel.controller;

import com.dailycodework.lakesidehotel.model.Room;
import com.dailycodework.lakesidehotel.response.RoomResponse;
import com.dailycodework.lakesidehotel.service.IRoomService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
    private final IRoomService roomService;



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
}
