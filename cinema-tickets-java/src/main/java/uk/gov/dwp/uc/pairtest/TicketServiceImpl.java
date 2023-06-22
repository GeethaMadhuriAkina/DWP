package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
	private final TicketPaymentService paymentService;
    private final SeatReservationService reservationService;
    private static final int MAX_TICKETS_PER_PURCHASE = 20;

    public TicketServiceImpl(TicketPaymentService paymentService, SeatReservationService reservationService) {
        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        // Validate the purchase request
        boolean adultTicketRequested = false;
        int totalAdultTickets = 0;
        int totalChildTickets = 0;
        int totalInfantTickets = 0;

        for (TicketTypeRequest request : ticketTypeRequests) {
            int noOfTickets = request.getNoOfTickets();
            TicketTypeRequest.Type ticketType = request.getTicketType();

            switch (ticketType) {
                case ADULT:
                    adultTicketRequested = true;
                    totalAdultTickets += noOfTickets;
                    break;
                case CHILD:
                    totalChildTickets += noOfTickets;
                    break;
                case INFANT:
                    totalInfantTickets += noOfTickets;
                    break;
            }
        }

        // Check for invalid purchase requests
        
        if ( totalAdultTickets==0 && totalChildTickets ==0 && totalInfantTickets==0 ) {
            throw new InvalidPurchaseException("Please select at least one ticket to proceed.");
        }

        if ( totalAdultTickets==0 && (totalChildTickets >0 || totalInfantTickets>0 )) {
            throw new InvalidPurchaseException("child or Infant tickets cannot be purchased without Adult tickets.");
        }
        if (totalAdultTickets>0 && (totalInfantTickets>totalAdultTickets) ) {
        	 throw new InvalidPurchaseException("Number of Infants cannot be more than Adults.");
        }

        // Calculate the total amount to pay and the total seats to reserve based on the ticket type requests
        int totalAmountToPay = (totalAdultTickets * 20) + (totalChildTickets * 10);
        int totalSeatsToReserve = totalAdultTickets + totalChildTickets;

        // Validate the total number of tickets
        if (totalSeatsToReserve > MAX_TICKETS_PER_PURCHASE) {
            throw new InvalidPurchaseException("Maximum of " + MAX_TICKETS_PER_PURCHASE + " tickets can be purchased at a time.");
        }

        // Make payment using the payment service
        paymentService.makePayment(accountId, totalAmountToPay);

        // Reserve seats using the reservation service
        reservationService.reserveSeat(accountId, totalSeatsToReserve);

        // If everything is successful, process the purchase
        System.out.println("Tickets purchased successfully!");
        System.out.println("Total amount paid: " + totalAmountToPay);
        System.out.println("Seats reserved: " + totalSeatsToReserve);
    }

}
