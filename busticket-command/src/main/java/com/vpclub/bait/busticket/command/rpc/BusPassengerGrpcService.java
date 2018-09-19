package com.vpclub.bait.busticket.command.rpc;

import com.vpclub.bait.busticket.api.BusPassengerRpcServiceGrpc;
import com.vpclub.bait.busticket.api.BusPassengerProto;
import com.vpclub.bait.busticket.api.entity.BusPassenger;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BusPassengerGrpcService {
    @Autowired
    BusPassengerRpcServiceGrpc.BusPassengerRpcServiceBlockingStub busPassengerRpcServiceBlockingStub;

    /**
     * 根据身份证查询乘客信息
     * @param userId
     * @param idNumber
     * @return
     */
    public List<BusPassenger> queryByIdNumber(String userId,String idNumber){
        BusPassengerProto.BusPassengerRequest request = BusPassengerProto.BusPassengerRequest.newBuilder().setUserId(userId).setIdNumber(idNumber).build();
        BusPassengerProto.BusPassengerResponse response = busPassengerRpcServiceBlockingStub.queryByIdNumber(request);
        String message = response.getMessage();
        List<BusPassenger> passengers = new ArrayList<>();
        JSONArray jsonArray = JSONArray.fromObject(message);
        if(jsonArray!=null && jsonArray.size()>0){
            for (int i = 0; i < jsonArray.size(); i++) {
                BusPassenger busPassenger = new BusPassenger();
                JSONObject js = jsonArray.getJSONObject(i);
                busPassenger.setIdNumber(js.getString("idNumber"));
                busPassenger.setOrderValue(js.getInt("orderValue"));
                busPassenger.setPassengerId(js.getLong("id"));
                busPassenger.setPassengerName(js.getString("name"));
                busPassenger.setUserId(js.getString("userId"));
                busPassenger.setUpdateTime(new Timestamp(js.getLong("updatedTime")));
                passengers.add(busPassenger);
            }
        }else {

        }
        return passengers;
    }

    /**
     * 根据用户id查询乘客信息
     * @param userId
     * @return
     */
    public List<BusPassenger> queryByuserId(String userId){
        BusPassengerProto.BusPassengerRequest request = BusPassengerProto.BusPassengerRequest.newBuilder().setUserId(userId).build();
        BusPassengerProto.BusPassengerResponse response = busPassengerRpcServiceBlockingStub.queryByUserId(request);
        String message = response.getMessage();
        List<BusPassenger> passengers = new ArrayList<>();
        JSONArray jsonArray = JSONArray.fromObject(message);
        if(jsonArray!=null && jsonArray.size()>0){
            for (int i = 0; i < jsonArray.size(); i++) {
                BusPassenger busPassenger = new BusPassenger();
                JSONObject js = jsonArray.getJSONObject(i);
                busPassenger.setIdNumber(js.getString("idNumber"));
                busPassenger.setOrderValue(js.getInt("orderValue"));
                busPassenger.setPassengerId(js.getLong("id"));
                busPassenger.setPassengerName(js.getString("name"));
                busPassenger.setUserId(js.getString("userId"));
                busPassenger.setUpdateTime(new Timestamp(js.getLong("updatedTime")));
                passengers.add(busPassenger);
            }
        }else {

        }
        return passengers;
    }

    /**
     * 新增乘客信息
     * @param busPassenger
     * @return
     */
    public boolean create(BusPassenger busPassenger){
        Long time = new Date().getTime();
        BusPassengerProto.BusPassengerRequest request = BusPassengerProto.BusPassengerRequest.newBuilder().
                setIdNumber(busPassenger.getIdNumber()).setId(busPassenger.getPassengerId()).
                setName(busPassenger.getPassengerName()).setOrderValue(busPassenger.getOrderValue()).
                setUserId(busPassenger.getUserId()).setCreatedBy(time).setCreatedTime(time).setUpdatedBy(time).setUpdatedTime(time).build();
        BusPassengerProto.BusPassengerResponse response = busPassengerRpcServiceBlockingStub.create(request);
        String message = response.getMessage();
        return Boolean.valueOf(message);
    }

    /**
     * 根据乘客id删除乘客信息
     * @param id
     * @return
     */
    public boolean delete(Long id){
        Long time = new Date().getTime();
        BusPassengerProto.BusPassengerRequest request = BusPassengerProto.BusPassengerRequest.newBuilder().
                setId(id).build();
        BusPassengerProto.BusPassengerResponse response = busPassengerRpcServiceBlockingStub.delete(request);
        String message = response.getMessage();
        return Boolean.valueOf(message);
    }

    /**
     * 根据身份证查询乘客信息
     * @param busPassenger
     * @return
     */
    public boolean update(BusPassenger busPassenger){
        Long time = new Date().getTime();
        BusPassengerProto.BusPassengerRequest request = BusPassengerProto.BusPassengerRequest.newBuilder().
                setIdNumber(busPassenger.getIdNumber()).setId(busPassenger.getPassengerId()).
                setName(busPassenger.getPassengerName()).setOrderValue(busPassenger.getOrderValue()).
                setUserId(busPassenger.getUserId()).setUpdatedBy(time).setUpdatedTime(time).build();
        BusPassengerProto.BusPassengerResponse response = busPassengerRpcServiceBlockingStub.update(request);
        String message = response.getMessage();
        return Boolean.valueOf(message);
    }
}
