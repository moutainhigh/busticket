package com.vpclub.bait.busticket.command.aggregates;

import com.vpclub.bait.busticket.api.command.CreateBusCustomerInfoBatchCommand;
import com.vpclub.bait.busticket.api.command.CreateBusInsuranceInfoCommand;
import com.vpclub.bait.busticket.api.command.CreateBusOrderInfoCommand;
import com.vpclub.bait.busticket.api.command.UpdateBusInsuranceInfoCommand;
import com.vpclub.bait.busticket.api.event.BusCustomerInfoBatchCreateEvent;
import com.vpclub.bait.busticket.api.event.BusInsuranceInfoCreateEvent;
import com.vpclub.bait.busticket.api.event.BusInsuranceInfoUpdateEvent;
import com.vpclub.bait.busticket.api.event.BusOrderInfoCreateEvent;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

/**
 * Created by chenwei on 2017/12/14.
 */
@Aggregate(repository = "busTicketAggregateRepository")
@ToString
@Slf4j
@NoArgsConstructor
public class BusTicketAggregate {
    @AggregateIdentifier
    private Long id;

    public BusTicketAggregate(CreateBusOrderInfoCommand command){
        BusOrderInfoCreateEvent event = new BusOrderInfoCreateEvent();
        BeanUtils.copyProperties(command,event);

        apply(event);
    }
    @EventHandler
    public void on(BusOrderInfoCreateEvent event){
        this.id = event.getId();
    }

    public BusTicketAggregate(CreateBusInsuranceInfoCommand command){
        BusInsuranceInfoCreateEvent event = new BusInsuranceInfoCreateEvent();
        BeanUtils.copyProperties(command,event);
        apply(event);
    }
    @EventHandler
    public void on(BusInsuranceInfoCreateEvent event){
        this.id = event.getId();
    }
    public BusTicketAggregate(CreateBusCustomerInfoBatchCommand command){
        BusCustomerInfoBatchCreateEvent event = new BusCustomerInfoBatchCreateEvent();
        BeanUtils.copyProperties(command,event);
        apply(event);
    }
    @EventHandler
    public void on(BusCustomerInfoBatchCreateEvent event){
        this.id = event.getId();
    }
    public BusTicketAggregate(UpdateBusInsuranceInfoCommand command){
        BusInsuranceInfoUpdateEvent event = new BusInsuranceInfoUpdateEvent();
        BeanUtils.copyProperties(command,event);
        apply(event);
    }
    @EventHandler
    public void on(BusInsuranceInfoUpdateEvent event){
        this.id = event.getId();
    }
}
