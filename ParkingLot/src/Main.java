import java.util.*;

enum SpotStatus {
    Available, Occupied, InMaintainance
}

enum VehicleType{
    TwoWheeler, Car, Truck
}

enum PaymentMode {
    UPI, CASH, CARD
}

abstract class Vehicle{
    String vehicleId;
    VehicleType type;

    public Vehicle(String vehicleId, VehicleType type){
        this.vehicleId=vehicleId;
        this.type=type;
    }

    abstract String getVehicleId();
    abstract VehicleType getVehicleType();
}

class TwoWheeler extends Vehicle{

    TwoWheeler(String vehicleId, VehicleType type){
        super(vehicleId,type);
    }

    @Override
    String getVehicleId(){
        return vehicleId;
    }

    @Override
    VehicleType getVehicleType(){
        return type;
    }
}

class Car extends Vehicle{

    Car(String vehicleId, VehicleType type){
        super(vehicleId,type);
    }

    @Override
    String getVehicleId(){
        return vehicleId;
    }

    @Override
    VehicleType getVehicleType(){
        return type;
    }
}

class Truck extends Vehicle{

    Truck(String vehicleId, VehicleType type){
        super(vehicleId,type);
    }

    @Override
    String getVehicleId(){
        return vehicleId;
    }

    @Override
    VehicleType getVehicleType(){
        return type;
    }
}

class VehicleFactory{
    static Vehicle createFactory(String vehicleNo, VehicleType type){
        switch (type){
            case Car: return new Car(vehicleNo,VehicleType.Car);
            case Truck: return new Truck(vehicleNo,VehicleType.Truck);
            case TwoWheeler: return new TwoWheeler(vehicleNo,VehicleType.TwoWheeler);
            default: throw new IllegalArgumentException("Invalid Vehicle Type");
        }
    }
}


class Spot{
    String spotId;
    SpotStatus spotStatus;
    VehicleType type;
    Vehicle vehicleParked;

    public Spot(String spotId, SpotStatus status, VehicleType type){
        this.spotId=spotId;
        this.spotStatus=status;
        this.type=type;
    }

    String getSpotId(){
        return spotId;
    }

    VehicleType getVehicleType(){
        return type;
    }

    SpotStatus getSpotStatus(){
        return this.spotStatus;
    }

    void freeSpot(){
        this.vehicleParked=null;
        this.spotStatus=SpotStatus.Available;
    }

    void assignVehicle(Vehicle vehicle){
        this.vehicleParked=vehicle;
        this.spotStatus=SpotStatus.Occupied;
    }
}

class DisplayBoard{
    static void displayBoard(List<Spot> spots){
        for(Spot spot:spots){
            System.out.println("Spot Status: "+ spot.getSpotStatus());
        }
    }
}

class Floor{
    String floorId;
    List<Spot> spots;
    DisplayBoard displayBoard;

    public Floor(String floorId, List<Spot> spots){
        this.floorId=floorId;
        this.spots=spots;
    }

    public Spot getAvailableSpot(VehicleType type){
        for(Spot spot:spots){
            if(spot.getVehicleType()==type && spot.getSpotStatus()==SpotStatus.Available){
                return spot;
            }
        }
        return null;
    }

}

class Ticket{
    String ticketId;
    Vehicle vehicle;
    Spot spot;
    Date entryTime;

    Ticket(String ticketId, Vehicle vehicle, Spot spot, Date entryTime){
        this.ticketId=ticketId;
        this.spot=spot;
        this.vehicle=vehicle;
        this.entryTime=entryTime;
    }
}

class Pricing{
    static Map<VehicleType,Double> priceMap=new HashMap<>();

    static {
        priceMap.put(VehicleType.TwoWheeler,20.0);
        priceMap.put(VehicleType.Car,30.0);
        priceMap.put(VehicleType.Truck,50.0);
    }

    static double getPrice(VehicleType vehicleType){
        return priceMap.get(vehicleType);
    }
}

class ParkingLot{
    static ParkingLot instance;
    List<Floor> floors;

    public ParkingLot(List<Floor> floors) {
        this.floors=floors;
    }

    static ParkingLot getInstance(List<Floor> floors){
        if(instance==null) instance=new ParkingLot(floors);
        return instance;
    }

    List<Floor> getFloors(){
        return floors;
    }
}

class Entry{
    public Ticket issueTicket(Vehicle vehicle, ParkingLot lot){
        List<Floor> floors=lot.getFloors();
        for(Floor floor:floors) {
            Spot spot = floor.getAvailableSpot(vehicle.getVehicleType());
            if (spot != null) {
                spot.assignVehicle(vehicle);
                return new Ticket(UUID.randomUUID().toString(), vehicle, spot, new Date());
            }
        }
        return null;
    }
}

class Exit{
    void makePayment(Ticket ticket, PaymentMode mode){
        if(ticket == null){
            System.out.println("No ticket found! Cannot proceed with payment.");
            return;
        }

        double amount=Pricing.getPrice(ticket.vehicle.getVehicleType());
        Payment payment=PaymentFactory.getPaymentMethod(mode);
        payment.pay(amount);
        ticket.spot.freeSpot();
    }
}

class ParkingLotService{
    ParkingLot lot;
    Entry entry;
    Exit exit;

    ParkingLotService(ParkingLot lot, Entry entry, Exit exit){
        this.lot=lot;
        this.entry=entry;
        this.exit=exit;
    }

    Ticket enterVehicle(String vehicleNo, VehicleType type){
        Vehicle vehicle=VehicleFactory.createFactory(vehicleNo,type);
        return entry.issueTicket(vehicle,lot);
    }

    void exitVehicle(Ticket ticket, PaymentMode mode){
        exit.makePayment(ticket,mode);
        return;
    }
}

/* ======= Payment Module ======= */

interface Payment {
    void pay(double amount);
}

class CardPayment implements Payment{
    @Override
    public void pay(double amount){
        System.out.println("Card Payment");
    }
}

class CashPayment implements Payment{
    @Override
    public void pay(double amount){
        System.out.println("Cash Payment");
    }
}

class UPIPayment implements  Payment{
    @Override
    public void pay(double amount){
        System.out.println("UPI Payment");
    }
}

class PaymentFactory{
    static Payment getPaymentMethod(PaymentMode mode){
        switch (mode){
            case UPI: return new UPIPayment();
            case CARD: return new CardPayment();
            case CASH: return new CashPayment();
            default: throw new IllegalArgumentException("Invalid Payment Mode");
        }
    }
}


public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        Spot s1=new Spot("S1",SpotStatus.Available,VehicleType.Car);
        Spot s2=new Spot("S2",SpotStatus.Available,VehicleType.TwoWheeler);
        Spot s3=new Spot("S3",SpotStatus.Available,VehicleType.Car);
        Spot s4=new Spot("S4",SpotStatus.Available,VehicleType.Truck);

        List<Spot> floor1Spots=Arrays.asList(s1,s2,s3,s4);
        Floor floor1=new Floor("F1", floor1Spots);

        Spot s5=new Spot("S5",SpotStatus.Available,VehicleType.Car);
        Spot s6=new Spot("S6",SpotStatus.Available,VehicleType.TwoWheeler);
        Spot s7=new Spot("S7",SpotStatus.Available,VehicleType.Car);
        Spot s8=new Spot("S8",SpotStatus.Available,VehicleType.Truck);

        List<Spot> floor2Spots=Arrays.asList(s5,s6,s7,s8);
        Floor floor2=new Floor("F2", floor2Spots);

        List<Floor> floors=Arrays.asList(floor1,floor2);

        ParkingLot lot=ParkingLot.getInstance(floors);
        Entry entry=new Entry();
        Exit exit=new Exit();

        ParkingLotService service=new ParkingLotService(lot,entry,exit);

        Ticket t1=service.enterVehicle("UP78XX",VehicleType.Car);
        System.out.println(t1);
        service.exitVehicle(t1,PaymentMode.UPI);


    }
}