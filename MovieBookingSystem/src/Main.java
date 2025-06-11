import java.util.*;

/*

FR
- User should be able to search shows
- User should be able to view shows
- User should be able to book show
- Different Payment Modes
- Notification on booking/cancelling
- Admin Support

 */

enum SeatStatus{
    Available, Booked, InMaintainance
}

enum SeatType{
    General, Delux, Luxury
}

enum PaymentMode{
    CASH, UPI, CARD
}

class User{
    String userId;
    String userName;

    public User(String userId, String userName){
        this.userId=userId;
        this.userName=userName;
    }
}

class Show{
    String showId;
    String showName;
    double duration;
    Date date;

    public Show(String showId, String showName, double duration, Date date){
        this.showId=showId;
        this.showName=showName;
        this.duration=duration;
        this.date=date;
    }
}

class Seat{
    String seatId;
    SeatStatus seatStatus;
    SeatType seatType;
    double price;

    public Seat(String seatId, SeatStatus seatStatus, SeatType seatType){
        this.seatId=seatId;
        this.seatStatus=seatStatus;
        this.seatType=seatType;
    }

    //getter setter for seatStatus
}

class Pricing{
    static Map<SeatType,Double> priceDb=new HashMap<>();

    static {
        priceDb.put(SeatType.General,200.0);
        priceDb.put(SeatType.Delux,300.0);
        priceDb.put(SeatType.Luxury,500.0);
    }

    static double getPrice(SeatType seatType){
        return priceDb.get(seatType);
    }
}

class Hall{
    String hallId;
    List<Seat> seats;
    List<Show> shows;

    public Hall(String hallId, List<Seat> seats, List<Show> shows){
        this.hallId=hallId;
        this.seats=seats;
        this.shows=shows;
    }
}

class Theater{
    String theaterId;
    String theatername;
    String location;
    List<Hall> halls;
}

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

class Booking{
    String bookingId;
    User user;
    Show show;
    Seat seat;
    PaymentMode paymentMode;

    public Booking(String bookingId, User user, Show show, Seat seat, PaymentMode paymentMode){
        this.bookingId=bookingId;
        this.user=user;
        this.show=show;
        this.seat=seat;
        this.paymentMode=paymentMode;
    }
}

class NotificationService{
    void sendBookingConfirmation(User user, Booking booking){
        System.out.println("Booking confirmed for "+user.userName+" Seat: "+booking.seat.seatId);
    }

    void cancelBookingConfirmation(User user, Booking booking){
        System.out.println("Booking CANCELLED for "+user.userName+" Seat: "+booking.seat.seatId);
    }
}

class SearchService{
    List<Show> searchShows(List<Show> shows, String showName, Date date){
        List<Show> res=new ArrayList<>();
        for(Show show:shows){
            if(show.showName.equalsIgnoreCase(showName) && show.date.equals(date)){
                res.add(show);
            }
        }
        return res;
    }
}

class MovieBookingService{
    NotificationService notificationService;
    SearchService searchService;
    Map<String,Booking> bookingDb=new HashMap<>();

    public MovieBookingService(){
        this.notificationService=new NotificationService();
        this.searchService=new SearchService();
    }

    public List<Show> searchShows(List<Show> shows, String showName, Date date){
        return searchService.searchShows(shows,showName,date);
    }

    public Booking bookSeat(User user, Show show, SeatType seatType, Seat seat, PaymentMode paymentMode){
        if(seat.seatStatus!=SeatStatus.Available){
            System.out.println("Seat not available");
            return null;
        }

        seat.seatStatus=SeatStatus.Booked;

        double price=Pricing.getPrice(seatType);
        String bookingId=UUID.randomUUID().toString();
        Booking booking=new Booking(bookingId,user,show,seat,paymentMode);

        Payment payment=PaymentFactory.getPaymentMethod(paymentMode);
        payment.pay(price);

        bookingDb.put(bookingId,booking);
        notificationService.sendBookingConfirmation(user,booking);
        return booking;
    }

    void cancelBooking(String bookingId){
        Booking booking=bookingDb.get(bookingId);
        if(booking==null){
            System.out.println("Invalid Booking ID");
            return;
        }

        booking.seat.seatStatus=SeatStatus.Available;

        notificationService.cancelBookingConfirmation(booking.user,booking);
        bookingDb.remove(bookingId);
    }
}

public class Main {
    public static void main(String[] args) {

        // Step 1: Create seats
        List<Seat> seats = new ArrayList<>();
        seats.add(new Seat("S1", SeatStatus.Available, SeatType.General));
        seats.add(new Seat("S2", SeatStatus.Available, SeatType.Delux));
        seats.add(new Seat("S3", SeatStatus.InMaintainance, SeatType.Luxury));
        seats.add(new Seat("S4", SeatStatus.Available, SeatType.Luxury));

        // Step 2: Create a show
        Show show = new Show("SH1", "Interstellar", 2.5, new Date());

        // Step 3: Create a hall with show and seats
        List<Show> shows = new ArrayList<>();
        shows.add(show);
        Hall hall = new Hall("H1", seats, shows);

        // Step 4: Create theater with hall
        List<Hall> halls = new ArrayList<>();
        halls.add(hall);
        Theater theater = new Theater();
        theater.theaterId = "T1";
        theater.theatername = "PVR";
        theater.location = "Delhi";
        theater.halls = halls;

        // Step 5: Create services
        MovieBookingService bookingService = new MovieBookingService();

        // Step 6: Create a user
        User user = new User("U1", "Mukul");

        // Step 7: Search show
        List<Show> resultShows = bookingService.searchShows(shows, "Interstellar", show.date);
        System.out.println("Found Shows: " + resultShows.size());

        // Step 8: Try booking a seat
        Seat selectedSeat = seats.get(1); // Delux, Available
        Booking booking = bookingService.bookSeat(user, show, selectedSeat.seatType, selectedSeat, PaymentMode.UPI);

        if (booking != null) {
            System.out.println("Booking successful. Booking ID: " + booking.bookingId);
        }

        // Step 9: Cancel booking
        if (booking != null) {
            bookingService.cancelBooking(booking.bookingId);
        }
    }
}