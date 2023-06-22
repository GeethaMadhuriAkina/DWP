
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;

public class Main {
    public static void main(String[] args) {
        // Create instances of the payment service and reservation service
        TicketPaymentService paymentService = new TicketPaymentServiceImpl();
        SeatReservationService reservationService = new SeatReservationServiceImpl();

        // Create an instance of the ticket service
        TicketService ticketService = new TicketServiceImpl(paymentService, reservationService);

        // Define the accountId
        Long accountId = 12345L;

        // Define the ticket type requests
        TicketTypeRequest adultTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        TicketTypeRequest childTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10);
        TicketTypeRequest infantTicketRequest = new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 10);

        try {
            // Purchase tickets
            ticketService.purchaseTickets(accountId, adultTicketRequest, childTicketRequest, infantTicketRequest);
        } catch (InvalidPurchaseException e) {
            System.out.println("Invalid purchase request: " + e.getMessage());
        }
    }
}
