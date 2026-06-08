package controller.web;

import common.exception.domain.NotFoundException;
import common.exception.infra.BadRequestException;
import domain.Contract;
import domain.ContractList;
import domain.Subscription;
import domain.SubscriptionList;
import infra.config.TossConfig;
import infra.web.Router;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentController {

    private final ContractList contractList;
    private final SubscriptionList subscriptionList;
    private final TossConfig tossConfig;
    private final Map<String, PaymentOrder> pendingOrders = new ConcurrentHashMap<>();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private record PaymentOrder(String contractId, String subscriptionNo, long amount) {}

    public PaymentController(ContractList contractList, SubscriptionList subscriptionList, TossConfig tossConfig) {
        this.contractList     = contractList;
        this.subscriptionList = subscriptionList;
        this.tossConfig       = tossConfig;
    }

    public void registerRoutes(Router router) {
        router.post("/payments/prepare", (req, res) -> {
            var body     = req.body(PrepareRequest.class);
            Contract contract = contractList.findBySubscriptionNo(body.subscriptionNo());
            if (contract == null) throw new NotFoundException("계약을 찾을 수 없습니다.");

            String orderId = "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            pendingOrders.put(orderId, new PaymentOrder(contract.getContractId(), body.subscriptionNo(), body.amount()));
            res.ok(Map.of("orderId", orderId));
        });

        router.post("/payments/confirm", (req, res) -> {
            var body  = req.body(ConfirmRequest.class);
            PaymentOrder order = pendingOrders.get(body.orderId());
            if (order == null) throw new BadRequestException("유효하지 않은 주문번호입니다.");
            if (order.amount() != body.amount()) throw new BadRequestException("결제 금액이 일치하지 않습니다.");

            confirmWithToss(body.paymentKey(), body.orderId(), body.amount());

            Contract contract = contractList.getByContractId(order.contractId());
            contract.activate();
            contractList.save(contract);

            Subscription subscription = subscriptionList.getByNo(order.subscriptionNo());
            subscription.contract();
            subscriptionList.save(subscription);

            pendingOrders.remove(body.orderId());

            res.ok(Map.of("contractId", contract.getContractId(), "policyNo", contract.getPolicyNo()));
        });
    }

    private void confirmWithToss(String paymentKey, String orderId, long amount) {
        try {
            String credentials = Base64.getEncoder()
                .encodeToString((tossConfig.secretKey() + ":").getBytes());
            String body = String.format(
                "{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":%d}",
                paymentKey, orderId, amount);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tossConfig.apiBaseUrl() + "/confirm"))
                .header("Authorization", "Basic " + credentials)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new BadRequestException("Toss 결제 승인 실패: " + response.body());
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Toss API 호출 실패: " + e.getMessage());
        }
    }

    private record PrepareRequest(String subscriptionNo, long amount) {}
    private record ConfirmRequest(String paymentKey, String orderId, long amount) {}
}
