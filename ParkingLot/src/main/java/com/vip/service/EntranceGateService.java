package com.vip.service;

import com.vip.common.dto.EntranceTicketDto;
import com.vip.entity.ParkingSpot;
import com.vip.entity.ParkingTicket;
import com.vip.entity.Vehicle;
import com.vip.exception.ErrorCode;
import com.vip.exception.ParkingLotException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
public class EntranceGateService {
    @Autowired
    private ParkingSpotService parkingSpotService;

    @Autowired
    private ParkingTicketService parkingTicketService;

    @Autowired
    private VehicleService vehicleService;

    public EntranceTicketDto generateParkingTicket(Vehicle vehicle, Long userId) {

        log.info("generateParkingTicket: Start generating parking ticket for vehicle No :{}",vehicle.getVehicleNo());
        try{/**
         * Here we are finding the existing vehicle, if it is not present then we are treating
         * the parameter vehicle as current vehicle
         */
        Vehicle currentVehicle = vehicleService.findExistingVehicle(vehicle);
        /**
         *  This method searches for an empty parking spot of the given vehicle type in the repository.
         *  If an available parking space is found, it is returned; otherwise, null is returned.
         */
        ParkingSpot parkingSpot = findParkingSpot(currentVehicle);
        if (parkingSpot == null) {
            log.info("generateParkingTicket: Stopped generating parking ticket for vehicle: {} as Parking lot is already full  for: {}",vehicle.getVehicleNo(), currentVehicle.getVehicleType().getDisplayName());
            throw new ParkingLotException("Parking lot is already full for " + currentVehicle.getVehicleType().getDisplayName(),
                    ErrorCode.PARKING_NOT_AVAILABLE);
        }

        /**
         *  This method associates the provided vehicle with the specified parking spot,
         *  generates a new parking ticket with the current entry time, and saves the ticket.
         *  The saved parking ticket is then returned.
         */
            ParkingTicket generatedTicket = saveAndGenerateTicket(currentVehicle, parkingSpot,userId);
        if(Objects.isNull(generatedTicket)){
            log.info("generateParkingTicket: Stopped generating parking ticket for vehicle as Vehicle {} is already parked in the lot",vehicle.getVehicleNo());
                throw new ParkingLotException("Vehicle " + vehicle.getVehicleNo() + " is already parked in the lot",
                        ErrorCode.VEHICLE_ALREADY_PARKED);
            }
        log.info("generateParkingTicket: Successfully generated  parking ticket for Vehicle no {} ",vehicle.getVehicleNo());

            /**
             * Here we are updating the parkingSpot with vehicle and updating its "isEmpty" column
             */
        parkingSpotService.updateParkingSpace(parkingSpot, false);

        return getEntranceTicketDto(currentVehicle, parkingSpot, generatedTicket);

        } catch (ParkingLotException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected error: " + ex.getMessage(), ex);
            throw new ParkingLotException("Internal Server Error", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private ParkingSpot findParkingSpot(Vehicle oldVehicle) {
        ParkingSpot parkingSpot= parkingSpotService.findParkingSpace(oldVehicle.getVehicleType());
        if(Objects.isNull(parkingSpot)){
            return null;
        }
        return parkingSpot;
    }

    private ParkingTicket saveAndGenerateTicket(Vehicle oldVehicle, ParkingSpot parkingSpot,Long userId) {
        parkingSpot.setVehicle(oldVehicle);
        ParkingTicket ticket = new ParkingTicket();
        ticket.setEntryTime(LocalDateTime.now());
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicle(oldVehicle);
        ticket.setUserId(userId);
        ParkingTicket generatedTicket = parkingTicketService.save(ticket);
        return generatedTicket;
    }

    private static EntranceTicketDto getEntranceTicketDto(Vehicle oldVehicle, ParkingSpot parkingSpot, ParkingTicket generatedTicket) {
        EntranceTicketDto entranceTicketDto= new EntranceTicketDto();
        entranceTicketDto.setParkingTicketId(generatedTicket.getId());
        entranceTicketDto.setEntryTime(generatedTicket.getEntryTime());
        entranceTicketDto.setParkingSpaceId(parkingSpot.getId());
        entranceTicketDto.setVehicleNumber(oldVehicle.getVehicleNo());
        entranceTicketDto.setVehicleType(oldVehicle.getVehicleType().getDisplayName());

        return entranceTicketDto;
    }




}
