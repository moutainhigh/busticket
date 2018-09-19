package com.vpclub.bait.busticket.command.handlers;

import com.vpclub.bait.busticket.api.command.CreateBusCustomerInfoBatchCommand;
import com.vpclub.bait.busticket.api.command.CreateBusInsuranceInfoCommand;
import com.vpclub.bait.busticket.api.command.CreateBusOrderInfoCommand;
import com.vpclub.bait.busticket.api.command.UpdateBusInsuranceInfoCommand;
import com.vpclub.bait.busticket.command.aggregates.BusTicketAggregate;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BusticketCommandHandler {
    @Autowired
    private Repository<BusTicketAggregate> busTicketAggregateRepository;

    @CommandHandler
    public void handle(CreateBusCustomerInfoBatchCommand command) throws Exception {
        log.debug("CreateBusCustomerInfoBatchCommand command :{}",command);
        busTicketAggregateRepository.newInstance(() -> new BusTicketAggregate(command));

    }

    @CommandHandler
    public void handle(CreateBusInsuranceInfoCommand command) throws Exception {
        log.debug("CreateBusInsuranceInfoCommand command :{}",command);
        busTicketAggregateRepository.newInstance(() -> new BusTicketAggregate(command));

    }

    @CommandHandler
    public void handle(CreateBusOrderInfoCommand command) throws Exception {
        log.debug("CreateBusOrderInfoCommand command :{}",command);
        busTicketAggregateRepository.newInstance(() -> new BusTicketAggregate(command));

    }

    @CommandHandler
    public void handle(UpdateBusInsuranceInfoCommand command) throws Exception {
        log.debug("UpdateBusInsuranceInfoCommand command :{}",command);
        busTicketAggregateRepository.newInstance(() -> new BusTicketAggregate(command));

    }
}
