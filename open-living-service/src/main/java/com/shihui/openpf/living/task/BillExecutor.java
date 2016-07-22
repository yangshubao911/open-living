package com.shihui.openpf.living.task;

import com.alibaba.fastjson.JSONObject;
import com.shihui.api.order.vo.MerchantCancelParam;
import me.weimi.api.commons.config.ConfigLoader;
import me.weimi.api.commons.config.DefaultConfigLoader;
import me.weimi.api.commons.util.ApiLogger;
import org.springframework.stereotype.Component;

import com.shihui.openpf.living.service.OrderService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
/**
 * Created by zhoutc on 2015/9/30.
 */
@Component
public class BillExecutor {
    private static final ScheduledExecutorService SEND_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(2);
    private static final ExecutorService LISTEN_EXECUTOR_SERVICE = Executors.newFixedThreadPool(10);
    private static final ExecuteListenTask LISTEN_TASK = new ExecuteListenTask();
    
    @Value("${interval_buy_bill}")
    private long INTERVAL_BUYBILL = 10L;

    private int BEGIN_FORBIDDEN_TIME_BUYBILL = 2;
    private int END_FORBIDDEN_TIME_BUYBILL = 4;

    @Resource
    OrderService orderService;
    @Resource
    RequestService requestService;
    @Resource
    ProduceService produceService;
    @Resource
    LogicFactory logicFactory;
    @Resource
    RechargeMerchantService rechargeMerchantService;
    @Resource
    MerchantOperatorService merchantOperatorService;
    @Resource
    MerchantOperatorPriceService merchantOperatorPriceService;
    @Resource
    MerchantGoodsService merchantGoodsService;
    @Resource
    OrderSystemService orderSystemService;
    @Resource
    LockService lockService;
    @Resource
    NoticeAssemble noticeAssemble;

//TODO

    @PostConstruct
    public void init() {
    	SEND_EXECUTOR_SERVICE.scheduleAtFixedRate(new ExecuteSubmitTask(), 0, INTERVAL_BUYBILL, TimeUnit.MINUTES);
    	SEND_EXECUTOR_SERVICE.scheduleAtFixedRate(new ExecuteFeedbackTask(), 0, INTERVAL_BUYBILL, TimeUnit.MINUTES);
    	LISTEN_EXECUTOR_SERVICE.submit(LISTEN_TASK);
//        ORDER_EXECUTOR_SERVICE.scheduleAtFixedRate(new ExecuteFailTask(), 0, INIT_DATA_WAIT_TIME, TimeUnit.MINUTES);
//        ORDER_EXECUTOR_SERVICE.scheduleAtFixedRate(new ExecuteRetryFailTask(), 1, INIT_DATA_WAIT_TIME, TimeUnit.MINUTES);
//        ORDER_EXECUTOR_SERVICE.scheduleAtFixedRate(new ExecuteUnNoticeTask(), 2, INIT_DATA_WAIT_TIME, TimeUnit.MINUTES);

    }

  //TODO
    /**
     * 处理话费发起充值未反馈完成的订单
     */
    private class ExecuteSubmitTask implements Runnable {
    	public final static String CODIS_KEY_GAOYANG = "gaoyang";
    	
        @Override
        public void run() {
        	int errorcode;
            try {
            	ApiLogger.info("###############################RechargeTask BEGIN AT TIME--" + TimeUtil.getNowOfDateByFormat("yyyyMMddHHmmss"));
                //获取高阳订单处理权
                Long ll = lockService.IncreaseLock2(CODIS_KEY_GAOYANG);
            	boolean bGaoyang = (ll == 1 ? true : false);
                if(bGaoyang)
                	lockService.setExTime2(CODIS_KEY_GAOYANG);
                //发起充值失败的订单
                List<Order> failList = orderService.queryPaidOrder(bGaoyang);

                if (failList == null || failList.size() == 0) {
                    ApiLogger.info("ExecuteFailTask--NOT FOUND  CALL FAIL ORDER");
                    return;
                }
                ApiLogger.info("ExecuteFailTask--ORDERLIST SIZE:" + failList.size());
                for (Order order : failList) {
                	long orderid = order.getOrderid();
                    Long lock = lockService.IncreaseLock(orderid);
                    if (lock != 1) {
                        ApiLogger.info("RechargeTask--ORDERID:" + order.getOrderid() + " HAS DEAL IN OTHER THREAD!! : " + lock);
                        continue;
                    }
                    lockService.setExTime(orderid);
                    ApiLogger.info("ExecuteFailTask--ORDERID:" + order.getOrderid() +
                            " errorcode:" + order.getErrorcode()
                            + " retry:" + order.getRetry()
                            + " status:" + order.getStatus().getValue()
                            + " orderstatus:" + order.getStatus().getName());
                    try {
                        RetryResult retryResultBean = RetryOrder(order,bGaoyang);       
//////////////////////////////////////////////////////////////////////////////////////////////////                        
                        int retryResult = retryResultBean.getCode();
                        ApiLogger.info("$$$$$$$$$$$$$$#**#orderid:" + orderid + " retryResult:[" + retryResult + "] merchantid:["+order.getMerchantid() + "] phone:"+order.getAcountno());

                        switch(retryResult) {
                        case 0:
                        {
                        	errorcode = ErrorCodeEnum.DEFAULT.getValue();
                        	orderService.updateOrderRetry(orderid, order.getRetry() + 1, retryResultBean.getMerchantId(), errorcode, retryResultBean.getProviderId());
                        	ApiLogger.info("ExecuteFailTask####ORDERID:" + orderid + " RETRY SUCESS");
                        	break;
                        }
                        case 1:
                        {
                            MerchantCancelParam var = new MerchantCancelParam();
                            var.setMerchantId(retryResultBean.getMerchatCode());
                            var.setIsReturnShihui(1);
                            var.setIsNeedReview(2);
                            var.setRemark("话费充值退款");
                            var.setOrderId(orderid);
                            var.setOrderStatus(com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut);
                            var.setPrice(StringUtil.yuan2hao(order.getPay()));
                            var.setReason("充值失败");

                            if (orderSystemService.merchantCancelOrder(var)) {
                                ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " NOT FOUND MERCHANT ," +
                                        " REFUND SUCESS, update remote order!!");
                            } else {
                                ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " REFUND FAILED!!");
                            }
                        	break;
                        }
                        case 2:
                        {
                        	errorcode = ErrorCodeEnum.CallRechargeFail.getValue();
                        	orderService.updateOrderRetry(orderid, order.getRetry() + 1, retryResultBean.getMerchantId(), errorcode, retryResultBean.getProviderId());
                        	ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " CALL NEW MERCHANT RECHARGE FAILED!!");
                        	break;
                        }
                        case 3:
                        {
                        	errorcode = ErrorCodeEnum.CreateFail.getValue();
 
                            MerchantCancelParam var = new MerchantCancelParam();
                            var.setMerchantId(retryResultBean.getMerchatCode());
                            var.setIsReturnShihui(1);
                            var.setIsNeedReview(2);
                            var.setRemark("话费充值退款");
                            var.setOrderId(orderid);
                            var.setOrderStatus(com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut);
                            var.setPrice(StringUtil.yuan2hao(order.getPay()));
                            var.setReason("充值失败");

                        	//商户退款
                            if (orderSystemService.merchantCancelOrder(var)) {
                                //退款成功
                                ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " CALL NEW MERCHANT CREATE" +
                                        " ORDER FAIL, REFUND SUCESS, update remote order!!");
                            } else {
                                ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " REFUND FAILED!!!");
                            }
                            
                        	break;
                        }
                        case 4:
                        {
                            boolean saveException = orderService.saveErrorCode(orderid, ErrorCodeEnum.Exception.getValue());
                            ApiLogger.info("ExecuteFailTask--ORDERID:" + orderid + " save exception errorcode result:"
                                    + saveException);
                            //不能退款 异常情况
                            ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " DEAL EXCEPTION");
                        	break;
                        }
                        case 5:
                        {
                        	 errorcode = ErrorCodeEnum.DEFAULT.getValue();
                        	break;
                        }
                        case 6:
                        {
                        	 errorcode = ErrorCodeEnum.DEFAULT.getValue();
      	 
                        	 order.setErrorcode(com.shihui.phonerecharge.api.model.db.ErrorCodeEnum.DEFAULT);
                 			 order.setStatus(com.shihui.phonerecharge.api.model.db.OrderStatusEnum.Complete);
                			 order.setUpdatetime(TimeUtil.getNowOfDateByFormat("yyyyMMddHHmmss"));
                        	 orderService.updateStatusAndErrorcode(order);

                        	 MerchantGoods merchantGoods = merchantGoodsService.queryProvider(order.getProviderid());
                        	 
                             JSONObject settlementJson = new JSONObject();
                             settlementJson.put("settlePrice", StringUtil.yuan2hao(merchantGoods.getPrice()));
                             settlementJson.put("settleMerchantId", retryResultBean.getMerchatCode());

                        	 boolean payorderchange = orderSystemService.complete(order.getOrderid(), order.getPid(),
                        			 settlementJson.toJSONString(), com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut.getValue() );
                             ApiLogger.info("SUCCESS :  [" + order.getOrderid() + "] result: " + payorderchange);
                        	
                             break;
                        }
                        }
   
                    } catch (Exception e) {
                        ApiLogger.error("ExecuteFailTask############################ORDERID:" + order.getOrderid() + " ERROR!!", e);
                    }
                }

            }catch (Exception e){
                ApiLogger.error("ExecuteRechargeTask-------------error",e);
            }
            ApiLogger.info("###############################ExecuteRechargeTask END AT TIME:" + TimeUtil.getNowOfDateByFormat("yyyyMMddHHmmss"));
        }


        protected RetryResult RetryOrder(Order order, boolean bGaoyang) {
               long orderid = order.getOrderid();
               Merchant merchant = rechargeMerchantService.queryMerchant(order.getMerchantid());
               String tag = merchant.getTag();
               
               //高阳一秒查询一订单
               if(bGaoyang == true)
	               if(tag.compareTo(CODIS_KEY_GAOYANG) == 0)
	               {
	            	   try {
	            		   Thread.sleep(1000);
	            	   }
	            	   catch(Exception e) {
	            		   ApiLogger.info("RetryOrder--orderid:" + orderid + " -- " + e.getMessage());
	            		   return new RetryResult(5);
	            	   }
	               }
               RechargeProcess oldProcess = logicFactory.getProcess(tag);
               int checkResult = oldProcess.check(order);//0:success,1:fail,2:retry
               if (checkResult == 2) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " CAN'T RETRY");
                   return new RetryResult(4);

               } else if  (checkResult == 0) {
            	   return new RetryResult(6);
               } // checkResult == 1
               //如果已经经过2个供应商充值未成功，退款
               if( order.getRetry() > 0 )
            	   return new RetryResult(1, merchant.getMerchanrcode());
               //
               Produce produce = produceService.queryProduceByPID(order.getPid());

               int areaid = produce.getAreaid();
               int operatorid = produce.getOperatorid();
               int facePirce = produce.getFaceprice();
               List<Merchant> merchantList = rechargeMerchantService.getMerchant();

               if (merchantList == null || merchantList.size() == 0) {
                   ApiLogger.info("RetryOrder--" + areaid + "-" + operatorid
                           + "-" + facePirce + " unfound merchant ");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }
               List<MerchantOperator> merchantOperators = merchantOperatorService.
                       queryMerchantOperators(operatorid, merchantList);
               if (merchantOperators == null || merchantOperators.size() == 0) {
                   ApiLogger.info("RetryOrder--" + areaid + "-" + operatorid
                           + "-" + facePirce + " unfound merchant operator ");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }
               List<MerchantOperatorPrice> merchantOperatorPriceList =
                       merchantOperatorPriceService.queryMerchantOperatorPrices(produce.getFaceprice(),
                               merchantOperators);
               if (merchantOperatorPriceList == null || merchantOperatorPriceList.size() == 0) {
                   ApiLogger.info("RetryOrder--" + areaid + "-" + operatorid
                           + "-" + facePirce + " unfound merchant operator price");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }
               List<MerchantGoods> merchantGoodsList = merchantGoodsService.
                       queryMerchantGoods(areaid, merchantOperatorPriceList);
               if (merchantGoodsList == null || merchantGoodsList.size() == 0) {
                   ApiLogger.info("RetryOrder--" + areaid + "-" + operatorid
                           + "-" + facePirce + " unfound merchant operator price area");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }

               //按权重选择商户
               Map<Integer, Integer> choiceMap = new HashMap<>();
               Map<Integer, Merchant> merchantMap = new HashMap<>();
               Map<Integer, MerchantGoods> merchantGoodsMap = new HashMap<>();
               for (MerchantGoods goods : merchantGoodsList) {
                   int merchantid = goods.getMerchantid();
                   if (order.getMerchantid() == merchantid) continue;
                   Merchant dbmerchant = rechargeMerchantService.queryMerchant(merchantid);
                   if (dbmerchant != null &&
                           dbmerchant.getStatus().getValue() != SwitchEnum.Close.getValue()
                           && !StringUtil.isEmpty(dbmerchant.getTag())) {
                       choiceMap.put(merchantid, dbmerchant.getWeight());
                       merchantMap.put(merchantid, dbmerchant);
                       merchantGoodsMap.put(merchantid, goods);
                   }
               }
               int choiceMerchantid = ChoiceMerhantUtil.choiceMerchant(choiceMap);
               if (choiceMerchantid == -1) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " can't find other merchant ");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }

               MerchantGoods merchantGoods = merchantGoodsMap.get(choiceMerchantid);
               Merchant newmerchant = merchantMap.get(choiceMerchantid);
               String newtag = newmerchant.getTag();
               String settle = merchantGoods.getPrice();
               long merchartCode = newmerchant.getMerchanrcode();
               int merchantId = newmerchant.getMechantid();
               long providerId = merchantGoods.getProviderid();
               order.setMerchantid(choiceMerchantid);
               order.setProviderid(merchantGoods.getProviderid());
               if (newtag == null) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " CAN'T FOUND RETRY MERCHANT");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }

               ApiLogger.info("RetryOrder--orderid:" + orderid + " old merchant: " + merchant.getMechantname() + " choice new merchant :" + newmerchant.getMechantname());

               RechargeRequest rechargeRequest = requestService.queryOpenRequest(orderid);

               RechargeProcess newProcess = logicFactory.getProcess(newtag);

               if( rechargeRequest != null ) {
	               boolean closeOrder = oldProcess.closeRequest(rechargeRequest);
	               if (!closeOrder) {
	                   ApiLogger.info("RetryOrder--orderid:" + orderid + " CLOSE FAIL");
	                   return new RetryResult(4);
	
	               }
               }

               boolean retryCreate = newProcess.createOrder();
               if (!retryCreate) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " retryCreate FAIL");
                   return new RetryResult(3, merchartCode, settle, providerId, merchantId);
               }
               RechargeInfo rechargeInfo = new RechargeInfo(order.getAcountno(),
                       produce.getFaceprice(),
                       order.getOrderid(),
                       order.getMerchantid(),
                       produce.getOperatorid(),
                       produce.getAreaid(),
                       merchantGoods.getExternalid()
               );
               boolean retryCall = newProcess.callRecharge(rechargeInfo);
               if (!retryCall) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " retryCall FAIL");
                   return new RetryResult(2, merchartCode, settle, providerId, merchantId);
               }

               ApiLogger.info("RetryOrder--orderid:" + orderid + " RETRY SUCCESS!!!");

               return new RetryResult(0, merchartCode, settle, providerId, merchantId);

        }

    }
    /**
     * 处理话费发起充值失败订单
     */
    private class ExecuteFeedbackTask implements Runnable {

        @Override
        public void run() {
            try {
                ApiLogger.info("###############################FailTask BEGIN AT TIME--" + TimeUtil.getNowOfDateByFormat("yyyyMMddHHmmss"));
                long currentTimeMillis = System.currentTimeMillis();
                //发起充值失败的订单
                List<Order> rchargeOrderList = orderService.queryRechargeFailOrder();
                //通知充值失败的订单
                List<Order> noticeOrderList = orderService.queryNoticeFailOrder();
                List<Order> failList = new ArrayList<>();
                failList.addAll(rchargeOrderList);
                failList.addAll(noticeOrderList);

                if (failList == null || failList.size() == 0) {
                    ApiLogger.info("ExecuteFailTask--NOT FOUND  CALL FAIL ORDER");
                }
                ApiLogger.info("ExecuteFailTask--ORDERLIST SIZE:" + failList.size());
                for (Order order : failList) {
                    String key = order.getOrderid() + "_Fail";
                    Long lock = lockService.IncreaseLock(key);
                    if (lock != 1) {
                        ApiLogger.info("ExecuteFailTask--ORDERID:" + order.getOrderid() + " HAS DEAL IN OTHER THREAD!!");
                        continue;
                    }
                    lockService.setExTime(key);
                    ApiLogger.info("ExecuteFailTask--ORDERID:" + order.getOrderid() +
                            " errorcode:" + order.getErrorcode()
                            + " retry:" + order.getRetry()
                            + " orderstatus:" + order.getStatus().getName());
                    try {
                        long orderid = order.getOrderid();
                        if (order.getStatus().getValue() != OrderStatusEnum.Paid.getValue()) {
                            ApiLogger.info("ExecuteFailTask####orderid:" + orderid + "  status:" + order.getStatus().getName() + " error");
                            return;
                        }
                        int oldMerchantId = order.getMerchantid();

                        boolean updateRetry = orderService.saveErrorCode(orderid, 0, 1);
                        ApiLogger.info("ExecuteFailTask####orderid:" + orderid + " updateRetry:" + updateRetry);
                        if (!updateRetry) return;
                        RetryResult retryResultBean = RetryOrder(order);
                        int retryResult = retryResultBean.getCode();
                        ApiLogger.info("ExecuteFailTask####orderid:" + orderid + " retryResult:" + retryResult);

                        if (retryResult != 1 && retryResult != 4) {
                            int errorcode = ErrorCodeEnum.DEFAULT.getValue();
                            switch (retryResult) {
                                case 0:
                                    errorcode = ErrorCodeEnum.DEFAULT.getValue();
                                    break;
                                //   case 1 : errorcode = ErrorCodeEnum.DEFAULT.getValue();break;
                                case 2:
                                    errorcode = ErrorCodeEnum.CallRechargeFail.getValue();
                                    break;
                                case 3:
                                    errorcode = ErrorCodeEnum.CreateFail.getValue();
                                    break;
                    /*case 4:
                        errorcode = ErrorCodeEnum.Exception.getValue();
                        break;*/
                            }

                            boolean update = orderService.updateOrderRetry(orderid, 1, retryResultBean.getMerchantId(),
                                    errorcode, retryResultBean.getProviderId());
                            ApiLogger.info("ExecuteFailTask####ORDERID:" + orderid +
                                    " newMerchantId:" + retryResultBean.getMerchantId() +
                                    " errorcode:" + errorcode +
                                    " oldMerchantId:" + oldMerchantId +
                                    " update merchant and errorcode result:" + update);
                        }

                        MerchantCancelParam var = new MerchantCancelParam();
                        var.setMerchantId(retryResultBean.getMerchatCode());
                        var.setIsReturnShihui(1);
                        var.setIsNeedReview(2);
                        var.setRemark("话费充值退款");
                        var.setOrderId(orderid);
                        var.setOrderStatus(com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut);
                        var.setPrice(StringUtil.yuan2hao(order.getPay()));
                        var.setReason("充值失败");
                        if (retryResult == 0) {
                            //处理成功
                            ApiLogger.info("ExecuteFailTask####ORDERID:" + orderid + " RETRY SUCESS");
                            // String settle_fen = new BigDecimal(merchantGoods.getPrice()).multiply(new BigDecimal("100")).setScale(0).toString();


                      /*  boolean up_order = orderAssemble.updateOrder(orderid,
                                com.shihui.api.common.model.OrderStatusEnum.OrderUnConfirm,
                                JsonUtil.buildExtStatus(OrderStatusEnum.Paid));*/
                       /* boolean up_order = orderAssemble.updateOrder(orderid,
                                com.shihui.api.common.model.OrderStatusEnum.OrderUnConfirm,
                                JsonUtil.buildExtStatus(OrderStatusEnum.Paid)
                                , Long.parseLong(settle_fen),
                                newmerchant.getMerchanrcode(),
                                SettleMethodEnum.UnSettle
                        );*/
                            // ApiLogger.info("ExecuteFailTask####ORDERID:" + orderid + " RETRY SUCESS, update remote order:" + up_order);
                        } else if (retryResult == 1) {
                            if (orderSystemService.merchantCancelOrder(var)) {
                           /* boolean up_order = orderAssemble.updateOrder(orderid, com.shihui.api.common.model.OrderStatusEnum.OrderMrchantClose,
                                    JsonUtil.buildExtStatus(OrderStatusEnum.OrderMrchantClose));*/
                                ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " NOT FOUND MERCHANT ," +
                                        " REFUND SUCESS, update remote order!!");
                            } else {
                                ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " REFUND FAILED!!");
                            }
                        } else if (retryResult == 2) {

                            ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " CALL NEW MERCHANT RECHARGE FAILED!!");

                        } else if (retryResult == 3) {


                            //商户退款
                            if (orderSystemService.merchantCancelOrder(var)) {
                                //退款成功
                         /*   String settle_fen = new BigDecimal(merchantGoods.getPrice()).multiply(new BigDecimal("100")).setScale(0).toString();
*/
                         /*   boolean up_order = orderAssemble.updateOrder(orderid,
                                    com.shihui.api.common.model.OrderStatusEnum.OrderMrchantClose,
                                    JsonUtil.buildExtStatus(OrderStatusEnum.OrderMrchantClose)*/
                                // );
                         /*   boolean up_order = orderAssemble.updateOrder(orderid,
                                    com.shihui.api.common.model.OrderStatusEnum.OrderMrchantClose,
                                    JsonUtil.buildExtStatus(OrderStatusEnum.OrderMrchantClose)
                                    , Long.parseLong(settle_fen),
                                    newmerchant.getMerchanrcode(),
                                    SettleMethodEnum.UnSettle
                            );*/
                                ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " CALL NEW MERCHANT CREATE" +
                                        " ORDER FAIL, REFUND SUCESS, update remote order!!");
                            } else {
                                ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " REFUND FAILED!!!");
                            }
                        } else if (retryResult == 4) {

                            boolean saveException = orderService.saveErrorCode(orderid, ErrorCodeEnum.Exception.getValue());
                            ApiLogger.info("ExecuteFailTask--ORDERID:" + orderid + " save exception errorcode result:"
                                    + saveException);
                            //不能退款 异常情况
                            ApiLogger.info("ExecuteFailTask############################ORDERID:" + orderid + " DEAL EXCEPTION");
                        }
                    } catch (Exception e) {
                        ApiLogger.error("ExecuteFailTask############################ORDERID:" + order.getOrderid() + " ERROR!!", e);
                    }

                }

                ApiLogger.info("###############################FailTask END AT TIME:" + TimeUtil.getNowOfDateByFormat("yyyyMMddHHmmss"));
            }catch (Exception e){
                ApiLogger.error("FailTask-------------error",e);
            }
        }


        public RetryResult RetryOrder(Order order) {

               long orderid = order.getOrderid();
               Merchant merchant = rechargeMerchantService.queryMerchant(order.getMerchantid());
               String tag = merchant.getTag();
               RechargeProcess oldProcess = logicFactory.getProcess(tag);
               boolean retry = oldProcess.canRetry(order);
               if (!retry) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " CAN'T RETRY");
                   return new RetryResult(4);

               }

               Produce produce = produceService.queryProduceByPID(order.getPid());

               int areaid = produce.getAreaid();
               int operatorid = produce.getOperatorid();
               int facePirce = produce.getFaceprice();
               List<Merchant> merchantList = rechargeMerchantService.getMerchant();

               if (merchantList == null || merchantList.size() == 0) {
                   ApiLogger.info("RetryOrder--" + areaid + "-" + operatorid
                           + "-" + facePirce + " unfound merchant ");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }
               List<MerchantOperator> merchantOperators = merchantOperatorService.
                       queryMerchantOperators(operatorid, merchantList);
               if (merchantOperators == null || merchantOperators.size() == 0) {
                   ApiLogger.info("RetryOrder--" + areaid + "-" + operatorid
                           + "-" + facePirce + " unfound merchant operator ");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }
               List<MerchantOperatorPrice> merchantOperatorPriceList =
                       merchantOperatorPriceService.queryMerchantOperatorPrices(produce.getFaceprice(),
                               merchantOperators);
               if (merchantOperatorPriceList == null || merchantOperatorPriceList.size() == 0) {
                   ApiLogger.info("RetryOrder--" + areaid + "-" + operatorid
                           + "-" + facePirce + " unfound merchant operator price");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }
               List<MerchantGoods> merchantGoodsList = merchantGoodsService.
                       queryMerchantGoods(areaid, merchantOperatorPriceList);
               if (merchantGoodsList == null || merchantGoodsList.size() == 0) {
                   ApiLogger.info("RetryOrder--" + areaid + "-" + operatorid
                           + "-" + facePirce + " unfound merchant operator price area");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }

               //按权重选择商户
               Map<Integer, Integer> choiceMap = new HashMap<>();
               Map<Integer, Merchant> merchantMap = new HashMap<>();
               Map<Integer, MerchantGoods> merchantGoodsMap = new HashMap<>();
               for (MerchantGoods goods : merchantGoodsList) {
                   int merchantid = goods.getMerchantid();
                   if (order.getMerchantid() == merchantid) continue;
                   Merchant dbmerchant = rechargeMerchantService.queryMerchant(merchantid);
                   if (dbmerchant != null &&
                           dbmerchant.getStatus().getValue() != SwitchEnum.Close.getValue()
                           && !StringUtil.isEmpty(dbmerchant.getTag())) {
                       choiceMap.put(merchantid, dbmerchant.getWeight());
                       merchantMap.put(merchantid, dbmerchant);
                       merchantGoodsMap.put(merchantid, goods);
                   }
               }
               int choiceMerchantid = ChoiceMerhantUtil.choiceMerchant(choiceMap);
               //MerchantGoods merchantGoods = merchantGoodsMap.get(choiceMerchantid);
               // Merchant newmerchant = merchantMap.get(choiceMerchantid);
               if (choiceMerchantid == -1) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " can't find other merchant ");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }

               MerchantGoods merchantGoods = merchantGoodsMap.get(choiceMerchantid);
               Merchant newmerchant = merchantMap.get(choiceMerchantid);
               String newtag = newmerchant.getTag();
               String settle = merchantGoods.getPrice();
               long merchartCode = newmerchant.getMerchanrcode();
               int merchantId = newmerchant.getMechantid();
               long providerId = merchantGoods.getProviderid();
               order.setMerchantid(choiceMerchantid);
               order.setProviderid(merchantGoods.getProviderid());
               if (newtag == null) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " CAN'T FOUND RETRY MERCHANT");
                   return new RetryResult(1, merchant.getMerchanrcode());
               }

               ApiLogger.info("RetryOrder--orderid:" + orderid + " old merchant: " + merchant.getMechantname() + " choice new merchant :" + newmerchant.getMechantname());

               RechargeRequest rechargeRequest = requestService.queryOpenRequest(orderid);

               RechargeProcess newProcess = logicFactory.getProcess(newtag);

               if (rechargeRequest == null) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " CAN'T FOUND REQUEST");
                   return new RetryResult(4);

               }

               boolean closeOrder = oldProcess.closeRequest(rechargeRequest);
               if (!closeOrder) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " CLOSE FAIL");
                   return new RetryResult(4);

               }

               boolean retryCreate = newProcess.createOrder();
               if (!retryCreate) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " retryCreate FAIL");
                   return new RetryResult(3, merchartCode, settle, providerId, merchantId);
               }
               RechargeInfo rechargeInfo = new RechargeInfo(order.getAcountno(),
                       produce.getFaceprice(),
                       order.getOrderid(),
                       order.getMerchantid(),
                       produce.getOperatorid(),
                       produce.getAreaid(),
                       merchantGoods.getExternalid()
               );
               boolean retryCall = newProcess.callRecharge(rechargeInfo);
               if (!retryCall) {
                   ApiLogger.info("RetryOrder--orderid:" + orderid + " retryCall FAIL");
                   return new RetryResult(2, merchartCode, settle, providerId, merchantId);
               }

               ApiLogger.info("RetryOrder--orderid:" + orderid + " RETRY SUCCESS!!!");

               return new RetryResult(0, merchartCode, settle, providerId, merchantId);

        }

    }

    /**
     * 处理话费重试失败订单
     */
    public class ExecuteListenTask implements Runnable {


        @Override
        public void run() {
            try {
                ApiLogger.info("------------------------------RETRYFAIL BEGIN AT TIME--" + TimeUtil.getNowOfDateByFormat("yyyyMMddHHmmss"));
                List<Order> orderList = orderService.queryRetryFailOrder();
                ApiLogger.info("ExecuteRetryFailTask--orderlist size:" + orderList.size());
                for (Order order : orderList) {

                    String key = order.getOrderid() + "_Retry";
                    Long lock = lockService.IncreaseLock(key);
                    if (lock != 1) {
                        ApiLogger.info("ExecuteRetryFailTask--ORDERID:" + order.getOrderid() + " HAS DEAL IN OTHER THREAD!!");
                        continue;
                    }
                    lockService.setExTime(key);

                    ApiLogger.info("ExecuteRetryFailTask--orderid:" + order.getOrderid() +
                            " errorcode:" + order.getErrorcode()
                            + " retry:" + order.getRetry()
                            + " orderstatus:" + order.getStatus().getName());
                    Merchant merchant = rechargeMerchantService.queryMerchant(order.getMerchantid());
                    MerchantGoods dbMerchantGoods = merchantGoodsService.queryProvider(order.getProviderid());
                    switch (order.getErrorcode()) {
                        case CallRechargeFail:
                            RechargeProcess rechargeProcess = logicFactory.getProcess(merchant.getTag());
                            boolean canRefund = rechargeProcess.canRetry(order);
                            if (!canRefund) {
                                boolean saveException = orderService.saveErrorCode(order.getOrderid(), ErrorCodeEnum.Exception.getValue());
                                ApiLogger.info("ExecuteRetryFailTask--orderid:" + order.getOrderid()
                                        + " can't refund, save exception errorcode result:" + saveException);
                                return;
                            } else {
                                break;
                            }
                        default:
                            break;
                    }
                    MerchantCancelParam var = new MerchantCancelParam();
                    var.setMerchantId(merchant.getMerchanrcode());
                    var.setIsReturnShihui(1);
                    var.setIsNeedReview(2);
                    var.setRemark("话费充值退款");
                    var.setOrderId(order.getOrderid());
                    var.setOrderStatus(com.shihui.api.order.common.enums.OrderStatusEnum.OrderUnStockOut);
                    var.setPrice(StringUtil.yuan2hao(order.getPay()));
                    var.setReason("充值失败");
                    long orderid = order.getOrderid();
                    if (orderSystemService.merchantCancelOrder(var)) {
                        //退款成功
                  /*  boolean up_order = orderAssemble.updateOrder(orderid,
                            com.shihui.api.common.model.OrderStatusEnum.OrderMrchantClose,
                            JsonUtil.buildExtStatus(OrderStatusEnum.OrderMrchantClose)
                    );*/
                        ApiLogger.info("ExecuteNoticeFailTask-----------------------ORDERID:" + orderid + " REFUND SUCESS, update remote order");
                    } else {
                        ApiLogger.info("ExecuteNoticeFailTask-----------------------ORDERID:" + orderid + " REFUND FAILED");
                    }
                }

                ApiLogger.info("-----------------------------RETRYFAIL END AT TIME--" + TimeUtil.getNowOfDateByFormat("yyyyMMddHHmmss"));
            }catch (Exception e){
                ApiLogger.error("ExecuteNoticeFailTask---error",e);
            }
            }

    }

    /**
     * 处理话费未接收到充值结果通知订单
     */
    public class ExecuteReceiveTask implements Runnable {

        @Override
        public void run() {
            try {
                ApiLogger.info("*******************************ExecuteUnNoticeTask BEGIN AT TIME--" + TimeUtil.getNowOfDateByFormat("yyyyMMddHHmmss"));
                List<Order> orderList = orderService.queryUnNoticeOrder();
                ApiLogger.info("ExecuteUnNoticeTask**orderlist size:" + orderList.size());
                Map<Integer, Merchant> merchantMap = new HashMap<>();
                List<Merchant> merchantList = rechargeMerchantService.getAllMerchant();
                for (Merchant merchant : merchantList) {
                    merchantMap.put(merchant.getMechantid(), merchant);
                }
                for (Order order : orderList) {
                    String key = order.getOrderid() + "_UnNotice";
                    Long lock = lockService.IncreaseLock(key);
                    if (lock != 1) {
                        ApiLogger.info("ExecuteUnNoticeTask**ORDERID:" + order.getOrderid() + " HAS DEAL IN OTHER THREAD!!");
                        continue;
                    }
                    lockService.setExTime(key);
                    long orderid = order.getOrderid();
                    try {

                        Merchant merchant = merchantMap.get(order.getMerchantid());
                        if (merchant == null) {
                            ApiLogger.info("ExecuteUnNoticeTask**orderid:" + orderid + " not found merchant:" + order.getMerchantid());
                            continue;
                        }
                        if (StringUtil.isEmpty(merchant.getTag())) {
                            ApiLogger.info("ExecuteUnNoticeTask**orderid:" + orderid + " not found tag");
                            continue;
                        }
                        RechargeProcess rechargeProcess = logicFactory.getProcess(merchant.getTag());
                        if (rechargeProcess == null) {
                            ApiLogger.info("ExecuteUnNoticeTask**orderid:" + orderid + " not found process");
                            continue;
                        }
                        NoticeInfo noticeInfo = rechargeProcess.queryNotice(order);
                        if (noticeInfo == null) {
                            ApiLogger.info("ExecuteUnNoticeTask**orderid:" + orderid + " noticeInfo is null");
                            continue;
                        }
                        ApiLogger.info("ExecuteUnNoticeTask**orderid:" + orderid + " recharge result noticecode:" + noticeInfo.getCode()
                                + " requestid:" + noticeInfo.getRequestId());
                        if (noticeInfo.getCode() == 1) {
                            boolean dealNotice = noticeAssemble.dealNotice(noticeInfo);
                            ApiLogger.info("ExecuteUnNoticeTask**orderid:" + orderid + " deal notice result" + dealNotice);
                        }

                    } catch (Exception e) {
                        ApiLogger.info("ExecuteUnNoticeTask**orderid:" + order.getOrderid() + " exception!!!", e);
                    }
                }

                ApiLogger.info("*******************************ExecuteUnNoticeTask END AT TIME--" + TimeUtil.getNowOfDateByFormat("yyyyMMddHHmmss"));

            }catch (Exception e){
                ApiLogger.error("ExecuteUnNoticeTask----error",e);
            }
        }
    }

    public static void main(String[] args) {

    }
}
