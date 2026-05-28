package controller.web;

import controller.web.dto.ClaimResponse;
import domain.*;
import domain.common.Money;
import infra.web.Router;

import java.util.List;
import java.util.stream.Collectors;

public class ClaimController {

    private final ClaimList claimList;

    public ClaimController(ClaimList claimList) {
        this.claimList = claimList;
    }

    public void registerRoutes(Router router) {
        // CL-02: 지급 대기 목록
        router.get("/claims", (req, res) -> {
            List<ClaimResponse> result = claimList.findAwaitingPayment().getAll().stream()
                    .map(ClaimResponse::from)
                    .collect(Collectors.toList());
            res.ok(result);
        });

        // 클레임 상세
        router.get("/claims/{id}", (req, res) -> {
            Claim c = claimList.findById(req.pathVariable("id"));
            if (c == null) { res.error(404, "클레임을 찾을 수 없습니다."); return; }
            res.ok(ClaimResponse.from(c));
        });

        // CL-02: 손해액 산정
        router.put("/claims/{id}/assess", (req, res) -> {
            Claim c = claimList.findById(req.pathVariable("id"));
            if (c == null) { res.error(404, "클레임을 찾을 수 없습니다."); return; }

            AssessRequest body = req.body(AssessRequest.class);
            c.assess(
                    new Money(body.settlement(), "KRW"),
                    new Money(body.deductible(), "KRW")
            );
            claimList.save(c);
            res.ok(ClaimResponse.from(c));
        });

        // CL-04: 보험금 지급
        router.put("/claims/{id}/pay", (req, res) -> {
            Claim c = claimList.findById(req.pathVariable("id"));
            if (c == null) { res.error(404, "클레임을 찾을 수 없습니다."); return; }

            PayRequest body = req.body(PayRequest.class);
            if (!Claim.isValidAccountNumber(body.accountNo())) {
                res.error(400, "계좌번호는 14자리 이하여야 합니다.");
                return;
            }
            c.completePayment(body.bank(), body.accountNo());
            claimList.save(c);
            res.ok(ClaimResponse.from(c));
        });
    }

    private record AssessRequest(long settlement, long deductible) {}
    private record PayRequest(String bank, String accountNo) {}
}
